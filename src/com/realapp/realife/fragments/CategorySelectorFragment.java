package com.realapp.realife.fragments;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realapp.realife.R;
import com.realapp.realife.activities.MainActivity;
import com.realapp.realife.adapters.AppCheckListAdapter;
import com.realapp.realife.adapters.CategoryViewerAdapter;
import com.realapp.realife.gui.viewpager.AnimatedViewPager;
import com.realapp.realife.gui.viewpager.AnimatedViewPager.TransitionEffect;
import com.realapp.realife.loaders.CategorizedAppsLoader;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppID;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.HandlerNotifier;
import com.realapp.realife.util.Print;
import com.realapp.realife.util.client.WebSync;

public class CategorySelectorFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<List<IAppItem>> {

	public static final String REFRESH_GUI = "refreshgui";
	private static final String BTN_PRESSED = "Button Pressed";

	// Fragments index
	private static final int UNCATEGORIZED_FRAG = 0;

	private static int pressedBtnId = -1;

	protected static boolean visible = false;

	// GUI elements
	private static View rootView;
	private static AnimatedViewPager appListViewer;
	private CategoryViewerAdapter appViewAdapter;
	private static Button setCategoryBtn;
	public ProgressDialog loadingDialog;

	// The Loader's id (this id is specific to the ListFragment's LoaderManager)
	private static final int LOADER_ID = 22222;

	private final Handler handler = new SafeHandler(this);

	public CategorySelectorFragment() {
		super();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getSherlockActivity().getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.categories_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.close_categories:
			((MainActivity) getActivity())
					.switchContent(new AddictionMeasurerFragment());
			return true;

		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.categorize_apps, container, false);

		initializeGUI();

		getLoaderManager().initLoader(LOADER_ID, null, this);

		return rootView;
	}

	private void initializeGUI() {
		
		initViewer();
		
		initButtons();
			
		loadingDialog = ProgressDialog.show(getActivity(), "",
				"Loading installed packages...");
		loadingDialog.setIndeterminate(true);

	}

	private void initButtons() {
		setCategoryBtn = (Button) rootView
				.findViewById(R.id.selectCategoryButton);

		setCategoryBtn.setEnabled(false);
		setCategoryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                openSelectCategoryDialog();
			}

			private void openSelectCategoryDialog() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Select category");

				final CharSequence[] choiceList = new CharSequence[AppDBHelper.categories
						.size()];
				int index = 0;
				for (String cat : AppDBHelper.categories) {
					choiceList[index] = cat;
					index++;
				}

				int selected = 0; // does not select anything

				builder.setSingleChoiceItems(choiceList, selected,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.setCancelable(false);
				builder.setPositiveButton("Assign",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ListView lw = ((AlertDialog) dialog)
										.getListView();
								int checkedItem = lw.getCheckedItemPosition();
								(new SetCategory()).execute(checkedItem);

							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
				
			}
		});

		
	}

	private void initViewer() {
		appListViewer = (AnimatedViewPager) rootView
				.findViewById(R.id.viewpager);
		appListViewer.setTransitionEffect(TransitionEffect.ZoomIn);
		appViewAdapter = new CategoryViewerAdapter(this);
		appListViewer.setAdapter(appViewAdapter);
		appListViewer.setCurrentItem(UNCATEGORIZED_FRAG, true);
		appListViewer
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int page) {
						HandlerNotifier.notifyIntMessage(handler, BTN_PRESSED,
								page);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});		
	}

	@Override
	public void onStop() {
		super.onPause();
		visible = false;

	}

	@Override
	public void onStart() {
		super.onStart();
		visible = true;
	}

	private class SetCategory extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {

			int newCategoryID = params[0];

			int position = appListViewer.getCurrentItem();
			// get the app list adapter inside the applistviewer at the current
			// page
			AppCheckListAdapter adapter = ((CategoryViewerAdapter) appListViewer
					.getAdapter()).getListAdapter(position);

			List<AppID> apps = new ArrayList<AppID>();
			for (int i = 0; i < adapter.getCount(); i++) {
				if (adapter.getItem(i).isChecked()) {
					AppID app = adapter.getItem(i).getAppID();
					Print.debug("(CategoryManager) " + app.getAppName()
							+ " is going to be updated");
					app.setCategoryID(newCategoryID);
					apps.add(app);
				}
			}

			AppDBHelper.getInstance(getActivity()).updateCategoryTable(apps);

			// write on an xml file the new current category organization
			File f = AppDBHelper.getInstance(getActivity())
					.dumpCategoryMapOnXML();
			WebSync.uploadFile(f);

			return null;
		}

		@Override
		protected void onPreExecute() {
			loadingDialog = ProgressDialog.show(getActivity(), "",
					"Updating categories configuration");
		}

		@Override
		protected void onPostExecute(Void unused) {
			getLoaderManager().getLoader(LOADER_ID).forceLoad();
		}

	}
	
	public void setCategoryBtnEnabled(boolean enabled) {
		setCategoryBtn.setEnabled(enabled);
	}

	/**********************/
	/** LOADER CALLBACKS **/
	/**********************/

	@Override
	public Loader<List<IAppItem>> onCreateLoader(int id, Bundle args) {
		return new CategorizedAppsLoader(getActivity(), this);
	}

	@Override
	public void onLoadFinished(Loader<List<IAppItem>> loader,
			List<IAppItem> itemList) {
		appViewAdapter.setAppItems(itemList);
		appViewAdapter.notifyDataSetChanged();
		loadingDialog.hide();

	}

	@Override
	public void onLoaderReset(Loader<List<IAppItem>> loader) {
		appViewAdapter.setAppItems(null);
	}
	
	
	
	/**********************/
	/** SafeHandler Class **/
	/**********************/

	private static class SafeHandler extends Handler {

		private final WeakReference<Fragment> fragmentRef;

		public SafeHandler(Fragment fragment) {
			fragmentRef = new WeakReference<Fragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			if (fragmentRef.get() != null) {
				Print.debug("(CategoryManagerFragment Handler) Received a message to handle");
				Bundle b = msg.getData();

				if (b.containsKey(BTN_PRESSED)) {
					int btnId = (Integer) b.get(BTN_PRESSED);
					String titleText = "";
					if (btnId == 0) {
						titleText = AppDBHelper.categories.get(AppDBHelper
								.getCategoryIndex(AppDBHelper.UNCATEGORIZED));
					} else {
						titleText = AppDBHelper.categories.get(btnId - 1);
					}

					TextView titleTextView = (TextView) rootView
							.findViewById(R.id.app_name_label);
					titleTextView.setText(titleText);

					// change page

					int prevPosition = pressedBtnId;
					if (pressedBtnId >= 0) {
						((CategoryViewerAdapter) appListViewer.getAdapter())
								.getListAdapter(prevPosition).resetCheckboxes();
						setCategoryBtn.setEnabled(false);
					}

					// update pressed btn
					pressedBtnId = btnId;

					return;
				}
			}
			return;
		}

	}



}
