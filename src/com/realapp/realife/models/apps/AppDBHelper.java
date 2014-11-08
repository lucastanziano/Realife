package com.realapp.realife.models.apps;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import com.realapp.realife.R;
import com.realapp.realife.util.Print;
import com.realapp.realife.util.Security;
import com.realapp.realife.util.TimeUtil;
import com.realapp.realife.util.client.WebSync;
import com.realapp.realife.util.xml.XMLParser;
import com.realapp.realife.util.xml.XMLSerializer;

public class AppDBHelper extends SQLiteOpenHelper {

	public final static String EMPTY_VALUE = "empty";

	private static final String DATABASE_NAME = "realife_db.db";

	private static final int SCHEMA_VERSION = 31;

	private static AppDBHelper sInstance = null;

	public static final String SOCIAL = "Social";
	public static final String TEXTING = "Communication";
	public static final String BROWSING = "Internet & news";
	public static final String GAMING = "Gaming";
	public static final String APPS = "Tools";
	public static final String SYSTEM = "System";
	public static final String UNCATEGORIZED = "Uncategorized";

	private Context context;

	// the folder where store new xml file
	private static File xmlDir;
	private static String xmlCatLocalFile;


	public HashMap<String, Drawable> iconTable = new HashMap<String, Drawable>();

	public static List<String> categories = Arrays.asList(TEXTING, SOCIAL,
			APPS, BROWSING, GAMING, SYSTEM, UNCATEGORIZED);

	public static AppDBHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null) {
			sInstance = new AppDBHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	private AppDBHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);

		setContext(context);
		xmlDir = context.getFilesDir();
		xmlCatLocalFile = Security.getDeviceUniqueID(context)
				+ "_categories.xml";

	}

	public File getLocalXmlCategoryFile() {
		return new File(xmlDir, xmlCatLocalFile);
	}

	public File getRemoteXmlCategoryFile() {
		return new File(xmlDir, WebSync.CATEGORYFILE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY AUTOINCREMENT, {2} LONG NOT NULL, {3} TEXT NOT NULL, {4} INT NOT NULL, {5} INT NOT NULL);";
		db.execSQL(MessageFormat.format(sql, AppUsageTable.TABLE_NAME,
				AppUsageTable._ID, AppUsageTable.HOUR_DAY_TIMEMILLIS,
				AppUsageTable.PACKAGE_NAME, AppUsageTable.ACTIVE_TIME,
				AppUsageTable.USER_CHECKS));

		sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY AUTOINCREMENT, {2} INT NOT NULL,{3} TEXT, {4} TEXT NOT NULL);";
		db.execSQL(MessageFormat.format(sql, AppIDTable.TABLE_NAME,
				AppIDTable._ID, AppIDTable.CATEGORY_ID,
				AppIDTable.APP_NAME, AppIDTable.PACKAGE));
		initCategoryTable(db);

	}

	private void initCategoryTable(SQLiteDatabase db) {
	
		db.beginTransaction();
		try {
			for (AppID app : XMLParser.parseCategoryXMLfile(context,
					getLocalXmlCategoryFile())) {
				ContentValues values = new ContentValues();
				values.put(AppIDTable.CATEGORY_ID, app.getCategoryID());
				values.put(AppIDTable.APP_NAME, app.getAppName());
				values.put(AppIDTable.PACKAGE, app.getPackageName());
				db.insert(AppIDTable.TABLE_NAME, EMPTY_VALUE, values);
			}

			db.setTransactionSuccessful();
		} catch (SQLiteException sql) {
			Print.error(sql);
		} finally {
			db.endTransaction();
		}
		
	}


	public synchronized void updateCategoryTable(List<AppID> apps) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (AppID app : apps) {
				Print.debug("Updating category value: " + app.getAppName());

				ContentValues values = new ContentValues();
				values.put(AppIDTable.APP_NAME, app.getAppName());
				values.put(AppIDTable.CATEGORY_ID, app.getCategoryID());
				values.put(AppIDTable.PACKAGE, app.getPackageName());

				int updatedRows = db.update(AppIDTable.TABLE_NAME, values,
						AppIDTable.PACKAGE + "= \"" + app.getPackageName()
								+ "\"", null);

				// if no rows have been affected by update, it means it was a
				// new
				// value, so we proceed to insert it
				if (updatedRows == 0) {
					db.insert(AppIDTable.TABLE_NAME, null, values);
				}

			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Print.error(e);
		} finally {
			db.endTransaction();
		}
		db.close();
		return;

	}

	/**
	 * Write on an XML file the content of CategoryMap
	 */
	public File dumpCategoryMapOnXML() {
		List<AppID> apps = getAppIDList(null, AppIDTable.CATEGORY_ID);

		try {
			XMLSerializer.serializeCategoriesToXML(apps, xmlDir,
					xmlCatLocalFile);
		} catch (Exception e) {
			Print.error(new Throwable(
					"Error dumping the category map to XML file", e));
		}
		return new File(xmlDir, xmlCatLocalFile);
	}

	synchronized public List<AppID> getAppIDList(String where, String orderBy) {
		List<AppID> appIDList = new ArrayList<AppID>();
		try {
			SQLiteDatabase db = getReadableDatabase();

			Cursor dc = db.query(AppIDTable.TABLE_NAME,
					AppIDTable.COLUMNS, where, null, null, null, orderBy);

			if (dc.moveToFirst() && dc.getCount() > 0) {
				int appNameIdx = dc
						.getColumnIndex(AppIDTable.APP_NAME);
				int packageIdx = dc.getColumnIndex(AppIDTable.PACKAGE);
				int catIdIdx = dc
						.getColumnIndex(AppIDTable.CATEGORY_ID);


				do {
					String packageName = dc.getString(packageIdx);
					String appName = dc.getString(appNameIdx);
					int categoryID = dc.getInt(catIdIdx);

					appIDList.add(new AppID(appName, packageName,
							categoryID));

				} while (dc.moveToNext());

			}
			dc.close();
			db.close();

		} catch (SQLiteException sqle) {
			Print.error(sqle);
		}
		return appIDList;
	}

	public static int getCategoryIndex(String category) {
		int idx = categories.indexOf(category);
		if (idx >= 0) {
			return idx;
		}
		Print.error(new Throwable(
				"There is a bug in the code: can't find the category index!"));
		return -1;
	}

	public static String getCategoryName(int index) {
		return categories.get(index);
	}

	public AppID getAppID(String packageName) {
        List<AppID> apps = getAppIDList(AppIDTable.PACKAGE + " = \"" + packageName + "\"", null);
        if(apps.size()>0){
        	return apps.get(0);
        }
		String appName = resolveAppName(packageName);
		AppID newID = new AppID(appName, packageName,
				getCategoryIndex(UNCATEGORIZED));
		List<AppID> singleValueAppList = new ArrayList<AppID>();
		singleValueAppList.add(newID);
		updateCategoryTable(singleValueAppList);
		return newID;
	}

	private String resolveAppName(String packageName) {

		final PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		final String appName = (String) (ai != null ? pm
				.getApplicationLabel(ai) : "(unknown)");

		return appName;
	}

	public void updateAppUsageDB(Collection<AppUsageSample> sampleList) {

		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {
			Print.debug("(AppUsageDatabase) Updating n.entries: "
					+ sampleList.size());
			for (AppUsageSample sample : sampleList) {

				Cursor dc = db.query(AppUsageTable.TABLE_NAME,
						AppUsageTable.COLUMNS, AppUsageTable.PACKAGE_NAME
								+ " = \"" + sample.getPackageName() + "\" AND "
								+ AppUsageTable.HOUR_DAY_TIMEMILLIS + " = "
								+ sample.getHourDayTimeMillis(), null, null,
						null, null);

				if (dc.moveToFirst() && dc.getCount() > 0) {
					int activeTimeIdx = dc
							.getColumnIndex(AppUsageTable.ACTIVE_TIME);
					int userCheckIdx = dc
							.getColumnIndex(AppUsageTable.USER_CHECKS);

					int prevActiveTime = dc.getInt(activeTimeIdx);
					int prevUserChecks = dc.getInt(userCheckIdx);

					ContentValues dbEntry = new ContentValues();
					dbEntry.put(AppUsageTable.HOUR_DAY_TIMEMILLIS,
							sample.getHourDayTimeMillis());
					dbEntry.put(AppUsageTable.PACKAGE_NAME,
							sample.getPackageName());
					dbEntry.put(AppUsageTable.ACTIVE_TIME,
							sample.getActiveTime() + prevActiveTime);
					dbEntry.put(AppUsageTable.USER_CHECKS, sample.getAppUses()
							+ prevUserChecks);

					db.update(
							AppUsageTable.TABLE_NAME,
							dbEntry,
							AppUsageTable.PACKAGE_NAME + " = \""
									+ sample.getPackageName() + "\" AND "
									+ AppUsageTable.HOUR_DAY_TIMEMILLIS + " = "
									+ sample.getHourDayTimeMillis(), null);
					dc.close();
				}

				else {
					ContentValues dbEntry = new ContentValues();
					dbEntry.put(AppUsageTable.HOUR_DAY_TIMEMILLIS,
							sample.getHourDayTimeMillis());
					dbEntry.put(AppUsageTable.PACKAGE_NAME,
							sample.getPackageName());
					dbEntry.put(AppUsageTable.ACTIVE_TIME,
							sample.getActiveTime());
					dbEntry.put(AppUsageTable.USER_CHECKS, sample.getAppUses());
					db.insert(AppUsageTable.TABLE_NAME, null, dbEntry);
				}

			}
			db.setTransactionSuccessful();
		} catch (SQLiteException sql) {
			Print.error(sql);
		} finally {
			db.endTransaction();
			db.close();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + AppUsageTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AppIDTable.TABLE_NAME);
		Print.debug("Upgrading database " + DATABASE_NAME);
		onCreate(db);
	}

	public static void cleanDB(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + AppUsageTable.TABLE_NAME);
		Print.debug("Cleaning database");
		String sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY AUTOINCREMENT, {2} LONG NOT NULL, {3} TEXT NOT NULL,{4} INT NOT NULL);";
		db.execSQL(MessageFormat.format(sql, AppIDTable.TABLE_NAME,
				AppIDTable._ID, AppIDTable.CATEGORY_ID,
				AppIDTable.APP_NAME, AppIDTable.PACKAGE));
	}


	public List<AppUsageSample> getAppUsageSampleListToday(String orderBy) {
		return getAppUsageSampleList(AppUsageTable.HOUR_DAY_TIMEMILLIS + " >= "
				+ TimeUtil.getTodayMidnightDateMillis(), orderBy);
	}

	public List<AppUsageSample> getAppUsageSampleListYesterday(String orderBy) {

		long yesterdayStart = TimeUtil.getTodayMidnightDateMillis()
				- (24 * 60 * 60 * 1000);
		long yesterdayEnd = TimeUtil.getTodayMidnightDateMillis();
		return getAppUsageSampleList(AppUsageTable.HOUR_DAY_TIMEMILLIS + " >= "
				+ yesterdayStart + " AND " + AppUsageTable.HOUR_DAY_TIMEMILLIS
				+ " <= " + yesterdayEnd, orderBy);
	}

	public List<AppUsageSample> getAppUsageSampleListWeek(String orderBy) {
		long weekStart = TimeUtil.getTodayMidnightDateMillis()
				- (7 * 24 * 60 * 60 * 1000);
		return getAppUsageSampleList(AppUsageTable.HOUR_DAY_TIMEMILLIS + " > "
				+ weekStart, orderBy);
	}

	synchronized public List<AppUsageSample> getAppUsageSampleList(
			String where, String orderBy) {
		List<AppUsageSample> appUsageSampleList = new ArrayList<AppUsageSample>();
		try {
			SQLiteDatabase db = getReadableDatabase();

			Print.debug("(AppDBHelper), Querying the db..");
			Cursor dc = db.query(AppUsageTable.TABLE_NAME,
					AppUsageTable.COLUMNS, where, null, null, null, orderBy);

			if (dc.moveToFirst() && dc.getCount() > 0) {
				int hourDayTimeIdx = dc
						.getColumnIndex(AppUsageTable.HOUR_DAY_TIMEMILLIS);
				int packageIdx = dc.getColumnIndex(AppUsageTable.PACKAGE_NAME);
				int activeTimeIdx = dc
						.getColumnIndex(AppUsageTable.ACTIVE_TIME);
				int userCheckIdx = dc.getColumnIndex(AppUsageTable.USER_CHECKS);

				do {
					String packageName = dc.getString(packageIdx);
					long hourDayTimeMillis = dc.getLong(hourDayTimeIdx);
					int activeTime = dc.getInt(activeTimeIdx);
					int userChecks = dc.getInt(userCheckIdx);

					appUsageSampleList.add(new AppUsageSample(packageName,
							hourDayTimeMillis, activeTime, userChecks));

				} while (dc.moveToNext());

			}
			dc.close();
			db.close();

		} catch (SQLiteException sqle) {
			Print.error(sqle);
		}
		return appUsageSampleList;
	}

	private Context getContext() {
		return context;
	}

	private void setContext(Context context) {
		this.context = context;
	}

	public Drawable getPackageIcon(String packName) {
		if (packName == null) {
			Print.error(new Throwable(
					"Error in Tools.getPackageIcon: packName is null."));
		}
		if (iconTable.containsKey(packName)) {
			return iconTable.get(packName);
		}
		Drawable icon;
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo appInfo : packages) {
			if (appInfo.packageName.equals(packName)) {
				icon = appInfo.loadIcon(pm);
				iconTable.put(packName, icon);
				return icon;
			}
		}
		icon = context.getResources().getDrawable(R.drawable.ic_launcher);
		iconTable.put(packName, icon);
		return icon;
	}


	public long getTodayActiveTime() {
		long time = 0;
		List<AppUsageSample> sampleList = AppDBHelper.getInstance(getContext())
				.getAppUsageSampleListToday(AppUsageTable.PACKAGE_NAME);
		for (AppUsageSample sample : sampleList) {
			time += sample.getActiveTime();
		}
		return time;
	}

}