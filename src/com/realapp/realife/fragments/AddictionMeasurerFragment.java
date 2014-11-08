package com.realapp.realife.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.realapp.realife.R;
import com.realapp.realife.activities.MainActivity;
import com.realapp.realife.adapters.AppViewerAdapter;
import com.realapp.realife.adapters.TitleNavigationAdapter;
import com.realapp.realife.gui.CategorySwitcherBar;
import com.realapp.realife.loaders.AppUsageLoader;
import com.realapp.realife.models.GlobalStatistics;
import com.realapp.realife.models.SpinnerNavItem;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppStatistics;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.HandlerNotifier;
import com.realapp.realife.util.Print;

public class AddictionMeasurerFragment extends SherlockFragment implements
		LoaderManager.LoaderCallbacks<List<IAppItem>>, OnNavigationListener {

	public static final int TODAY = 0;
	public static final int YESTERDAY = 1;
	public static final int WEEK = 2;

	public static int NAV_DATA_PERIOD = TODAY;

	private static final int MODERATE_ADDICTION_THRESHOLD = 33;
	private static final int SEVERE_ADDICTION_THRESHOLD = 66;

	// Fragments index
	public static final int TEXTING_FRAG = 0;
	public static final int SOCIAL_FRAG = 1;
	public static final int APPS_FRAG = 2;
	public static final int WWW_FRAG = 3;
	public static final int GAME_FRAG = 4;

	public static final int DEFAULT_FRAG = APPS_FRAG;

	// Handler tags
	public static final String BTN_PRESSED_TAG = "Button Pressed";
	private static final String PAGE_CHANGED_TAG = "PageChanged";
	private static final String NAVIGATION_CHANGED_TAG = "NavChanged";
	public static final String REFRESH_GUI_TAG = "refreshgui";
	private static final String SHOW_PROG_DIALOG_TAG = "PDialog";
	private static final String DISMISS_PROG_DIALOG_TAG = "DismissPDialog";

	private static final String SAVED_CURRENT_PAGE = "Saved current page";

	private static int currentAppFragment = DEFAULT_FRAG;

	// The Loader's id (this id is specific to the ListFragment's LoaderManager)
	private static final int LOADER_ID = 19878;

	// GUI elements
	private static View rootView;
	private static CategorySwitcherBar categoryBar;
	private static ViewPager appListViewer;
	private static AppViewerAdapter appViewerAdapter;
	private ProgressBar progressGoalBar;
	private static ProgressDialog waitingDialog;

	private static Typeface HelveticaNeueUltraLightFont;

	private AppStatistics stats;

	private SafeHandler handler;

	public AddictionMeasurerFragment() {
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

		initHandler();
		initNavigationSpinner();
		loadTypefaceFonts();

	}

	private void initHandler() {
		handler = new SafeHandler(this);
	}

	private void initNavigationSpinner() {

		ActionBar actionBar = getSherlockActivity().getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		ArrayList<SpinnerNavItem> navSpinner = new ArrayList<SpinnerNavItem>();
		navSpinner.add(new SpinnerNavItem("Today"));
		navSpinner.add(new SpinnerNavItem("Yesterday"));
		navSpinner.add(new SpinnerNavItem("Week"));

		TitleNavigationAdapter adapter = new TitleNavigationAdapter(
				getActivity().getApplicationContext(), navSpinner);

		// assigning the spinner navigation
		actionBar.setListNavigationCallbacks(adapter, this);

	}

	private void loadTypefaceFonts() {
		HelveticaNeueUltraLightFont = getTypefaceFontByStringId(R.string.HelveticaNeueUltraLight);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.monitor_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.categories:
			((MainActivity) getActivity())
					.switchContent(new CategorySelectorFragment());
			return true;

		default:
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.addiction_measurer_layout,
				container, false);

		initializeVariables();
		initHeader();
		initAppViewPager();
		initializeGUI();
		initDataLoader();

		return rootView;
	}

	private void initializeVariables() {
		stats = new AppStatistics(new ArrayList<IAppItem>());
		currentAppFragment = DEFAULT_FRAG;
	}

	private void initHeader() {

		setCategoryAddictionIcon(stats.getMaxAddiction());

		setCategoryNameLabel(AppDBHelper.categories.get(DEFAULT_FRAG));
	}

	private static void setCategoryAddictionIcon(int addiction) {

		ImageView categoryAddictionIcon = (ImageView) rootView
				.findViewById(R.id.categoryAddictionImage);

		int iconRes = 0;

		if (addiction < MODERATE_ADDICTION_THRESHOLD) {
			iconRes = R.drawable.dep_soft;
		}
		if (addiction >= MODERATE_ADDICTION_THRESHOLD
				&& addiction < SEVERE_ADDICTION_THRESHOLD) {
			iconRes = R.drawable.dep_moderate;
		}
		if (addiction >= SEVERE_ADDICTION_THRESHOLD) {
			iconRes = R.drawable.dep_high;
		}

		categoryAddictionIcon.setImageResource(iconRes);

	}

	private static void setCategoryNameLabel(String categoryName) {

		TextView categoryNameLabel = (TextView) rootView
				.findViewById(R.id.categoryNameText);

		categoryNameLabel.setText(categoryName);
		categoryNameLabel.setTypeface(HelveticaNeueUltraLightFont);

	}

	private void initAppViewPager() {
		appListViewer = (ViewPager) rootView.findViewById(R.id.viewpager);

		appViewerAdapter = new AppViewerAdapter(this);
		appListViewer.setAdapter(appViewerAdapter);
		appListViewer.setCurrentItem(currentAppFragment, true);
		appListViewer.setOffscreenPageLimit(appViewerAdapter.getCount());
		appListViewer
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int page) {
						HandlerNotifier.notifyIntMessage(handler,
								PAGE_CHANGED_TAG, page);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

	}

	private void initializeGUI() {

		initProgressBar();
		initCategorySwitcherBar();
		initLabels();

		HandlerNotifier
				.notifyIntMessage(handler, BTN_PRESSED_TAG, DEFAULT_FRAG);
	}

	private void initCategorySwitcherBar() {
		categoryBar = new CategorySwitcherBar(rootView);
		categoryBar.setOnClickListeners(handler);
	}

	private void initProgressBar() {
		this.progressGoalBar = (ProgressBar) rootView
				.findViewById(R.id.positiveBar);
		this.progressGoalBar.setProgress(0);
	}

	private void initLabels() {
		Typeface labelFont = getTypefaceFontByStringId(R.string.HelveticaNeueItalic);

		TextView appNameLabel = (TextView) rootView
				.findViewById(R.id.app_name_label);
		appNameLabel.setTypeface(labelFont);

		TextView addictionLabel = (TextView) rootView
				.findViewById(R.id.app_addiction_label);
		addictionLabel.setTypeface(labelFont);
	}

	private Typeface getTypefaceFontByStringId(int stringId) {
		return Typeface.createFromAsset(getActivity().getAssets(),
				getActivity().getResources().getString(stringId));
	}

	private void initDataLoader() {
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	// onStart() is called after onStop()->onRestart()->onStart()
	@Override
	public void onStart() {
		super.onStart();

	}

	// onResume() is called after onPause()->onResume() OR onStart()->onResume()
	@Override
	public void onResume() {
		super.onResume();
	}

	// onPause() is called when the fragment exits from the foreground
	@Override
	public void onPause() {
		super.onPause();

		handler.removeCallbacks(null);

	}

	// onStop() is called when the app is not visible anymore
	@Override
	public void onStop() {
		super.onStop();
		handler.removeCallbacks(null);
	}

	// onDestroy() is called when the app is definitely closed
	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putInt(SAVED_CURRENT_PAGE, currentAppFragment);

	}

	/**********************/
	/** LOADER CALLBACKS **/
	/**********************/

	@Override
	public Loader<List<IAppItem>> onCreateLoader(int id, Bundle args) {
		Print.debug("+++ onCreateLoader() called! +++");
		return new AppUsageLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<IAppItem>> loader,
			List<IAppItem> data) {
		Print.debug("+++ onLoadFinished() called! +++");

		stats = new AppStatistics((List<IAppItem>) data);
		GlobalStatistics.getInstance().setGlobalStatistics(stats);
		GlobalStatistics.getInstance().updateAllTotalTimeText();
		Print.debug("(TodayFragment) Stats new totalActivetime "
				+ stats.getTotalActiveTime());

		appViewerAdapter.setAppItems(data);

		HandlerNotifier.notifyIntMessage(handler, REFRESH_GUI_TAG,
				stats.getMaxAddiction());

		if (waitingDialog != null) {
			waitingDialog.dismiss();
		}

	}

	@Override
	public void onLoaderReset(Loader<List<IAppItem>> loader) {
		Print.debug("+++ onLoadReset() called! +++");
		appViewerAdapter.setAppItems(new ArrayList<IAppItem>());
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		NAV_DATA_PERIOD = itemPosition;
		getLoaderManager().getLoader(LOADER_ID).forceLoad();
		HandlerNotifier.notifyEmptyMessage(handler, NAVIGATION_CHANGED_TAG);
		waitingDialog = ProgressDialog
				.show(getActivity(), "", "Loading data..");
		return true;
	}

	private static class SafeHandler extends Handler {

		private final WeakReference<AddictionMeasurerFragment> fragmentRef;

		public SafeHandler(AddictionMeasurerFragment fragment) {
			fragmentRef = new WeakReference<AddictionMeasurerFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			AddictionMeasurerFragment fragment = fragmentRef.get();
			if (fragment != null) {
				if (fragment.getActivity() != null) {
					Bundle bundle = msg.getData();

					if (bundle.containsKey(REFRESH_GUI_TAG)) {
						int addiction = (Integer) bundle.get(REFRESH_GUI_TAG);
						updateHeaderAddictionIcon(addiction);
						updateAppViewPagerContent();
						return;
					}

					if (bundle.containsKey(NAVIGATION_CHANGED_TAG)) {
						clearAppViewPagerContent();
						return;
					}

					if (bundle.containsKey(SHOW_PROG_DIALOG_TAG)) {
						String dialogMsg = (String) bundle
								.get(SHOW_PROG_DIALOG_TAG);
						waitingDialog = ProgressDialog.show(
								fragment.getActivity(), "", dialogMsg);
						return;
					}

					if (bundle.containsKey(DISMISS_PROG_DIALOG_TAG)) {
						waitingDialog.dismiss();
						return;
					}

					if (bundle.containsKey(PAGE_CHANGED_TAG)) {
						int btnId = (Integer) bundle.get(PAGE_CHANGED_TAG);
						updateHeaderCategoryName(btnId);
						animateButtons(fragment, btnId);
						return;
					}

					if (bundle.containsKey(BTN_PRESSED_TAG)) {
						int btnId = (Integer) bundle.get(BTN_PRESSED_TAG);
						updateHeaderCategoryName(btnId);
						animateButtons(fragment, btnId);
						updateAppViewerPage();
						return;
					}
					return;
				}
			}
		}

		private void updateAppViewerPage() {
			if (appListViewer.getCurrentItem() != currentAppFragment) {
				appListViewer.setCurrentItem(currentAppFragment);
			}
			return;
		}

		private void updateAppViewPagerContent() {

			for (int i = 0; i < appViewerAdapter.getCount(); i++) {
				ListView listV = (ListView) appListViewer
						.findViewWithTag(AppViewerAdapter
								.getListViewTagByPosition(i));
				ImageView imageV = (ImageView) appListViewer
						.findViewWithTag(AppViewerAdapter
								.getImageViewTagByPosition(i));
				appViewerAdapter.updateDataList(listV, imageV, i);
			}

			appViewerAdapter.notifyDataSetChanged();
			return;
		}

		private void updateHeaderAddictionIcon(int addiction) {
			setCategoryAddictionIcon(addiction);
		}

		private void updateHeaderCategoryName(int categoryId) {
			String categoryName = AppDBHelper.getCategoryName(categoryId);
			setCategoryNameLabel(categoryName);
		}

		private void clearAppViewPagerContent() {
			for (int i = 0; i < appViewerAdapter.getCount(); i++) {

				ListView listV = (ListView) appListViewer
						.findViewWithTag(AppViewerAdapter
								.getListViewTagByPosition(i));
				ImageView imageV = (ImageView) appListViewer
						.findViewWithTag(AppViewerAdapter
								.getImageViewTagByPosition(i));

				appViewerAdapter.clearPage(listV, imageV);
			}
			appViewerAdapter.notifyDataSetChanged();
			return;
		}

		private static void animateButtons(AddictionMeasurerFragment fragment,
				int newPage) {

			categoryBar.animateButton(fragment.getActivity(), newPage);

			if (currentAppFragment != newPage) {
				categoryBar.switchOffPreviousButton(fragment.getActivity(),
						currentAppFragment);
			}

			currentAppFragment = newPage;
		}

	}

}
