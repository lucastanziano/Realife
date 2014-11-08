package com.realapp.realife.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.realapp.realife.R;
import com.realapp.realife.ReaLifeApplication;
import com.realapp.realife.activities.MainActivity;
import com.realapp.realife.models.alerts.AlertEntry;
import com.realapp.realife.models.alerts.ConfigDatabaseHelper;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppUsageSample;
import com.realapp.realife.observers.AlertsObserver;
import com.realapp.realife.util.Print;
import com.realapp.realife.util.StringFormat;
import com.realapp.realife.util.TimeUtil;

public class MonitorService extends Service {

	final static int MONITOR_ID = 1987;
	final static int PERIOD = 1; // seconds
	public final static int FLUSH_COUNTDOWN = 20; // seconds
	private int alert_id = 1988;

	private HashMap<String, AppUsageSample> appUsageSamplesMap = new HashMap<String, AppUsageSample>();
	private static long todayActiveTime = 0; // time showed in the notification
												// bar
	private static int day = -1;
	private static boolean wasScreenOn = false;
	private static int phoneChecks = 0;
	private static String lastPackageUsed = "";
	private static int flushCountDown = FLUSH_COUNTDOWN;

	private AlertsObserver alertsObserver;

	private final Handler handler = new Handler();

	private final Runnable updateTask = new Runnable() {
		@Override
		public void run() {
			updateTaskList();
			handler.postDelayed(updateTask, PERIOD * 1000L);
		}
	};
	private HashMap<String, Integer> alertCountdownsMap = new HashMap<String, Integer>();

	@Override
	public void onCreate() {
		super.onCreate();

		initService();

		startForeground(
				MONITOR_ID,
				buildDefaultNotification("ReaLife", R.drawable.ic_launcher,
						"Running"));

		Print.debug("Service creating");

		handler.post(updateTask);

	}

	private void initService() {
		todayActiveTime = AppDBHelper.getInstance(getApplicationContext())
				.getTodayActiveTime();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		day = cal.get(Calendar.DAY_OF_MONTH);
		// load alerts
		loadAppAlerts();

		alertsObserver = new AlertsObserver(this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Print.debug("Service destroying");
		handler.removeCallbacks(updateTask);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	private void updateTaskList() {
		
		String packageName = getPackageOnForeground();

		if (isScreenON()) {

			updateScreenUnlockCounts();
			updateLocalTodayActiveTime();
//			updateAlertsCountdown(packageName);  this feature is not complete yet
			updatePackageActiveTime(packageName);   
			updatePackageUsesCounter(packageName);

			// when the countdown ends, it saves the data in the database
			flushCountDown--;
			if (flushCountDown <= 0) {
				flushCountDown = FLUSH_COUNTDOWN;
				updateDataBase();
			}
		} else {
			wasScreenOn = false;
		}

	}

	private String getPackageOnForeground() {
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getPackageName();
	}

	private void updatePackageUsesCounter(String packageName) {
		if (!lastPackageUsed.equals(packageName)) {
			appUsageSamplesMap.get(packageName).incrementUserChecks();
			lastPackageUsed = packageName;
		}
	}

	private void updatePackageActiveTime(String packageName) {
		if (appUsageSamplesMap.containsKey(packageName)) {
			// update active time
			appUsageSamplesMap.get(packageName).incrementActiveTime();

		} else {
			appUsageSamplesMap.put(packageName, new AppUsageSample(packageName,
					TimeUtil.getCurrentHourDayTimeMillis(), 1, 0));
		}
	}

	private void updateAlertsCountdown(String packageName) {
		if (alertCountdownsMap.containsKey(packageName)) {
			int count = alertCountdownsMap.get(packageName) - 1;
			alertCountdownsMap.put(packageName, count);
			if (count <= 0) {
				triggerAlert(packageName);
				SQLiteDatabase db = ConfigDatabaseHelper.getInstance(
						getApplicationContext()).getWritableDatabase();
				ConfigDatabaseHelper.resetAlert(this, packageName);
				db.close();
				loadAppAlerts();
			}
		}
	}

	private void updateLocalTodayActiveTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);
		if (currentDay != day) {
			todayActiveTime = 0;
		} else {
			todayActiveTime++;
		}
		day = currentDay;
	}

	private void updateScreenUnlockCounts() {
		if (wasScreenOn == false) {
			wasScreenOn = true;
			phoneChecks++;
		}

	}

	private boolean isScreenON() {
		PowerManager powermanager;
		powermanager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		return powermanager.isScreenOn();
	}

	private void triggerAlert(String packageName) {
		String appName = AppDBHelper.getInstance(getApplicationContext())
				.getAppID(packageName).getAppName();
		SQLiteDatabase db = ConfigDatabaseHelper.getInstance(
				getApplicationContext()).getReadableDatabase();
		AlertEntry ae = ConfigDatabaseHelper.getAlert(db, packageName);
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(
				alert_id++,
				buildDefaultNotification(
						"ReaLife Alert",
						R.drawable.ic_launcher,
						"You spent "
								+ StringFormat
										.getTimeFormatFromSeconds(ae.startCount)
								+ " using " + appName));
	}

	private void updateDataBase() {

		ConfigDatabaseHelper.getInstance(getApplicationContext())
				.updateCountdowns(alertCountdownsMap);

		AppDBHelper.getInstance(getApplicationContext()).updateAppUsageDB(
				appUsageSamplesMap.values());
		appUsageSamplesMap.clear();

		updateNotificationMessage();
		// send a signal to the front end that the db has been updated
		broadcastSignalDatabaseUpdated();

	}

	private void broadcastSignalDatabaseUpdated() {
		Intent intent = new Intent();
		intent.setAction(ReaLifeApplication.ACTION_DB_UPDATED);
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Update the notification bar text
	 */
	private void updateNotificationMessage() {

		String newMsg = "Today: "
				+ StringFormat.getTimeFormatFromSeconds(todayActiveTime);

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(
				MONITOR_ID,
				buildDefaultNotification("ReaLife", R.drawable.ic_launcher,
						newMsg));

	}

	private Notification buildDefaultNotification(String title, int iconId,
			String msg) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(iconId).setContentTitle(title)
				.setContentText(msg);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0,
				new Intent(this, MainActivity.class), 0);
		mBuilder.setContentIntent(contentIntent);

		return mBuilder.build();
	}

	public void loadAppAlerts() {
		// load alerts
		SQLiteDatabase db = ConfigDatabaseHelper.getInstance(
				getApplicationContext()).getReadableDatabase();
		alertCountdownsMap = ConfigDatabaseHelper.loadCountdowns(db);
		db.close();
	}

	public static boolean isRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (MonitorService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
