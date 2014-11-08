package com.realapp.realife.loaders;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.realapp.realife.fragments.CategorySelectorFragment;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppID;
import com.realapp.realife.models.apps.AppIDTable;
import com.realapp.realife.models.apps.AppIDItem;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.Print;

public class CategorizedAppsLoader extends BaseLoader {

	
	private WeakReference<Fragment> fragRef;


	public CategorizedAppsLoader(Context ctx, Fragment fragment) {
		super(ctx);
		 fragRef = new WeakReference<Fragment>(fragment);
	}

	/****************************************************/
	/** (1) A task that performs the asynchronous load **/
	/****************************************************/

	@Override
	public List<IAppItem> loadInBackground() {
		long startTime = System.currentTimeMillis();
		List<IAppItem> itemList = new ArrayList<IAppItem>();
		List<AppID> appIDs = AppDBHelper.getInstance(getContext()).getAppIDList(null, AppIDTable.APP_NAME);
		
		appIDs = filterAppsNotInstalledOnDevice(appIDs);
		
		final int totalApps = appIDs.size();
		
		Print.debug("CATEGORY LOADER: num appIds found in db " + appIDs.size());
		int appLoadedCounter = 0;
		for (AppID appInfo : appIDs) {

			String packageName = appInfo.getPackageName();

				Drawable icon = AppDBHelper.getInstance(getContext())
							.getPackageIcon(packageName);
				
				itemList.add(new AppIDItem(icon, appInfo));
				
			appLoadedCounter++;
			final int appCounter = appLoadedCounter;
			if(fragRef.get() != null){
				fragRef.get().getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						((CategorySelectorFragment)fragRef.get()).loadingDialog.setMessage("Loading installed packages ("+ appCounter +"/" + totalApps + ")");
						
					}
				}); 
			}

		}
		Print.debug("Time to load all packages: " + (System.currentTimeMillis() - startTime));
		return itemList;
	}

	private List<AppID> filterAppsNotInstalledOnDevice(List<AppID> appIDs) {
		Set<String> installedPackages = new HashSet<String>();
		
		//store all the packages installed on the device in an HashSet
		PackageManager pm = getContext().getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo appInfo : packages) {
			installedPackages.add(appInfo.packageName);
		}
		
		//check if the app is installed querying the HashSet
		for(int i=0; i<appIDs.size(); i++){
			String packageName = appIDs.get(i).getPackageName();
			if(!installedPackages.contains(packageName)){
				appIDs.remove(i);
			}
		}
		return appIDs;
	}


}