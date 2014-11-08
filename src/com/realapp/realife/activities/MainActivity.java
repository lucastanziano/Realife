package com.realapp.realife.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.realapp.realife.R;
import com.realapp.realife.fragments.AddictionMeasurerFragment;
import com.realapp.realife.fragments.FancyBackgroundFragment;
import com.realapp.realife.service.MonitorService;

public class MainActivity extends SlidingFragmentActivity {

	private Fragment fragmentContent;

	private Handler handler;

	public void checkMonitorServiceAndStart() {
		if (!MonitorService.isRunning(getApplicationContext())) {
			startService(new Intent(this, MonitorService.class));
		}

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.app_name);
		handler = new Handler();
		checkMonitorServiceAndStart();

		initGUI(savedInstanceState);

	}

	private void initGUI(Bundle savedInstanceState) {
		setContentView(R.layout.content_frame);

		setupMenuFrame();

		setupViewFragment(savedInstanceState);

		setupBehindViewFragment();

		customizeSlidingMenu();

	}

	private void customizeSlidingMenu() {

		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

	}

	private void setupBehindViewFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new FancyBackgroundFragment())
				.commit();

	}

	private void setupViewFragment(Bundle savedInstanceState) {

		if (savedInstanceState != null)
			fragmentContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (fragmentContent == null)
			fragmentContent = new AddictionMeasurerFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragmentContent).commit();

	}

	private void setupMenuFrame() {
		
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);			
		} 
		else {
			View dummyView = new View(this);
			setBehindContentView(dummyView);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		getSlidingMenu().setSlidingEnabled(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "fragmentContent", fragmentContent);
	}

	public void switchContent(final Fragment fragment) {
		fragmentContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		handler.postDelayed(changeMenuFrame, 50);
	}

	private final Runnable changeMenuFrame = new Runnable() {
		public void run() {
			getSlidingMenu().showContent();
		}
	};

}
