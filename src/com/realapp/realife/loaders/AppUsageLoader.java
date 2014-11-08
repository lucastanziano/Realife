package com.realapp.realife.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;

import com.realapp.realife.ReaLifeApplication;
import com.realapp.realife.fragments.AddictionMeasurerFragment;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppID;
import com.realapp.realife.models.apps.AppIDItem;
import com.realapp.realife.models.apps.AppUsageItem;
import com.realapp.realife.models.apps.AppUsageSample;
import com.realapp.realife.models.apps.AppUsageTable;
import com.realapp.realife.models.apps.IAppItem;

public class AppUsageLoader extends BaseLoader {


	/****************************************************/
	/** (1) A task that performs the asynchronous load **/
	/****************************************************/

	public AppUsageLoader(Context ctx) {
		super(ctx);
		filter = new IntentFilter(
					ReaLifeApplication.ACTION_DB_UPDATED);

	}

	/**
	 * This method is called on a background thread and generates a List of
	 * {@link AppUsageSample} objects. Each entry corresponds to a single
	 * installed application on the device.
	 */
	@Override
	public List<IAppItem> loadInBackground() {
		List<IAppItem> appUsageList = new ArrayList<IAppItem>();
		
		List<AppUsageSample> sampleList;

		switch (AddictionMeasurerFragment.NAV_DATA_PERIOD) {
		case AddictionMeasurerFragment.TODAY:
			sampleList = AppDBHelper.getInstance(getContext())
					.getAppUsageSampleListToday(AppUsageTable.PACKAGE_NAME);
			break;
		case AddictionMeasurerFragment.YESTERDAY:
			sampleList = AppDBHelper.getInstance(getContext())
					.getAppUsageSampleListYesterday(AppUsageTable.PACKAGE_NAME);
			break;
		case AddictionMeasurerFragment.WEEK:
			sampleList = AppDBHelper.getInstance(getContext())
					.getAppUsageSampleListWeek(AppUsageTable.PACKAGE_NAME);
			break;
		default:
			sampleList = AppDBHelper.getInstance(getContext())
					.getAppUsageSampleListToday(AppUsageTable.PACKAGE_NAME);
		}

		if (sampleList.size() > 0) {
			List<AppUsageSample> appSampleList = new ArrayList<AppUsageSample>();
			String lastPackageName = sampleList.get(0).getPackageName();

			for (AppUsageSample sample : sampleList) {
				if (!sample.getPackageName().equals(lastPackageName)) {
					// new package found in the list, we save the information
					// and reset local variables
					AppID appID = AppDBHelper.getInstance(getContext())
							.getAppID(lastPackageName);
					
					Drawable icon = AppDBHelper.getInstance(
							getContext()).getPackageIcon(
									appID.getPackageName());
					AppIDItem appUsage = new AppUsageItem(icon, appID, appSampleList);
					appUsageList.add(appUsage);
					appSampleList = new ArrayList<AppUsageSample>();
				}
				appSampleList.add(sample);
				lastPackageName = sample.getPackageName();
			}
			AppID appID = AppDBHelper.getInstance(getContext()).getAppID(
					lastPackageName);
			
			Drawable icon = AppDBHelper.getInstance(
					getContext()).getPackageIcon(
							appID.getPackageName());
			
			AppUsageItem appUsage = new AppUsageItem(icon, appID, appSampleList);
			appUsageList.add(appUsage);
		}
        
		return appUsageList;
	}



}