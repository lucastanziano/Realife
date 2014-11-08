package com.realapp.realife.models.alerts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realapp.realife.util.Print;

public class ConfigDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "config_stats.db";

	private static final int SCHEMA_VERSION = 1;

	private static ConfigDatabaseHelper sInstance = null;

	public static ConfigDatabaseHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (sInstance == null) {
			sInstance = new ConfigDatabaseHelper(
					context.getApplicationContext());
		}
		return sInstance;
	}

	private ConfigDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE {0} ({1} INTEGER PRIMARY KEY AUTOINCREMENT, {2} TEXT NOT NULL, {3} INT NOT NULL,{4} INT NOT NULL);";
		db.execSQL(MessageFormat.format(sql, AlertTable.TABLE_NAME,
				AlertTable._ID, AlertTable.PACKAGE, AlertTable.COUNTDOWN,
				AlertTable.START_COUNT));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + AlertTable.TABLE_NAME);
		Print.debug("Upgrading statistics database " + DATABASE_NAME);
		onCreate(db);

	}

	/**
	 * Query the db for the alert with given package name
	 * 
	 * @param db
	 * @param packageName
	 * @return
	 */
	public static AlertEntry getAlert(SQLiteDatabase db, String packageName) {
		AlertEntry ae = null;
		Cursor dc = db.query(AlertTable.TABLE_NAME, AlertTable.COLUMNS,
				AlertTable.PACKAGE + " = \"" + packageName + "\"", null, null,
				null, null);
		if (dc.moveToFirst() && dc.getCount() > 0) {
			int scIdx = dc.getColumnIndex(AlertTable.START_COUNT);
			int startCount = dc.getInt(scIdx);
			ae = new AlertEntry(packageName, 0, startCount);
		}
		dc.close();
		return ae;
	}

	/**
	 * Insert a new alert or update an old one if already exists.
	 * 
	 * @param db
	 * @param entry
	 */
	public static void insertAlert(SQLiteDatabase db, AlertEntry entry) {
		db.beginTransaction();
		try {

			ContentValues v = new ContentValues();
			v.put(AlertTable.PACKAGE, entry.packageName);
			v.put(AlertTable.COUNTDOWN, entry.countDown);
			v.put(AlertTable.START_COUNT, entry.startCount);

			int i = db.update(AlertTable.TABLE_NAME, v, AlertTable.PACKAGE
					+ " = \"" + entry.packageName + "\"", null);
			if (i == 0) {
				db.insert(AlertTable.TABLE_NAME, null, v);
			}

			db.setTransactionSuccessful();

		} catch (Exception e) {
			Print.error(e);
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * Delete the alert with the given package name
	 * 
	 * @param db
	 * @param entry
	 */
	public static void removeAlert(SQLiteDatabase db, String packageName) {
		db.beginTransaction();
		try {
			db.delete(AlertTable.TABLE_NAME, AlertTable.PACKAGE + " = \""
					+ packageName + "\"", null);

			db.setTransactionSuccessful();

		} catch (Exception e) {
			Print.error(e);
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * Reset the countdown for a specific package
	 * 
	 * @param db
	 * @param packageName
	 */
	public static void resetAlert(Context context, String packageName) {
		SQLiteDatabase db = getInstance(context).getWritableDatabase();
		db.beginTransaction();
		try {
			Cursor dc = db.query(AlertTable.TABLE_NAME, AlertTable.COLUMNS,
					buildWhereClause(new String[] { packageName }), null, null,
					null, null);
			if (dc.moveToFirst() && dc.getCount() > 0) {

				int scIdx = dc.getColumnIndex(AlertTable.START_COUNT);
				int startCount = dc.getInt(scIdx);
				ContentValues v = new ContentValues();
				v.put(AlertTable.PACKAGE, packageName);
				v.put(AlertTable.COUNTDOWN, startCount);
				v.put(AlertTable.START_COUNT, startCount);

				int i = db.update(AlertTable.TABLE_NAME, v, AlertTable.PACKAGE
						+ " = \"" + packageName + "\"", null);
				if (i > 1) {
					Print.error(new Throwable(
							"Updating process failed: expected 1, received "
									+ i + ". Details: " + packageName));
				}
			}
			dc.close();
			db.setTransactionSuccessful();

		} catch (Exception e) {
			Print.error(e);
		} finally {
			db.endTransaction();
			db.close();
		}

	}

	/**
	 * Decrement the countdowns
	 * 
	 * @param db
	 * @param countdowns
	 *            A map containing package names as key and a number to
	 *            decrement at the countdown as object
	 */
	public List<AlertEntry> updateCountdowns(HashMap<String, Integer> countdowns) {

		SQLiteDatabase db = getWritableDatabase();
		List<AlertEntry> aExpired = new ArrayList<AlertEntry>();
		if (countdowns.isEmpty()) {
			return aExpired;
		}
		String[] packages = new String[countdowns.keySet().size()];
		int k = 0;
		for (String s : countdowns.keySet()) {
			packages[k] = s;
			k++;
		}
		db.beginTransaction();
		try {
			Cursor dc = db.query(AlertTable.TABLE_NAME, AlertTable.COLUMNS,
					buildWhereClause(packages), null, null, null, null);
			if (dc.moveToFirst() && dc.getCount() > 0) {
				int pnIdx = dc.getColumnIndex(AlertTable.PACKAGE);
				String packageName = dc.getString(pnIdx);
				int countIdx = dc.getColumnIndex(AlertTable.COUNTDOWN);
				int countDown = dc.getInt(countIdx)
						- countdowns.get(packageName);
				int scIdx = dc.getColumnIndex(AlertTable.START_COUNT);
				int startCount = dc.getInt(scIdx);

				if (countDown <= 0) {
					aExpired.add(new AlertEntry(packageName, countDown,
							startCount));
				}

				ContentValues v = new ContentValues();
				v.put(AlertTable.PACKAGE, packageName);
				v.put(AlertTable.COUNTDOWN, countDown);
				v.put(AlertTable.START_COUNT, startCount);
				int i = db.update(AlertTable.TABLE_NAME, v, AlertTable.PACKAGE
						+ " = \"" + packageName + "\"", null);
				if (i != 1) {
					Print.error(new Throwable(
							"(UpdateCountdowns) Updated more rows than expected at "
									+ packageName));
				}
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Print.error(e);
		} finally {
			db.endTransaction();
			db.close();
		}
		return aExpired;
	}

	/**
	 * Concatenate package names for a WHERE clause
	 * 
	 * @param packages
	 * @return
	 */
	private static String buildWhereClause(String[] packages) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < packages.length - 1; i++) {
			sb.append(AlertTable.PACKAGE + " = \"" + packages[i] + "\" OR ");
		}
		sb.append(AlertTable.PACKAGE + " = \"" + packages[packages.length - 1]
				+ "\"");
		return sb.toString();
	}

	/**
	 * Load countdowns from alerts. NB: in the db the countdown value passed is
	 * the countdown column value and not the startcount. Perform a reset before
	 * you want it.
	 * 
	 * @param db
	 * @return
	 */
	public static HashMap<String, Integer> loadCountdowns(SQLiteDatabase db) {
		HashMap<String, Integer> countdowns = new HashMap<String, Integer>();
		Cursor dc = db.query(AlertTable.TABLE_NAME, AlertTable.COLUMNS, null,
				null, null, null, null);
		if (dc.moveToFirst() && dc.getCount() > 0) {
			int pnIdx = dc.getColumnIndex(AlertTable.PACKAGE);
			String packageName = dc.getString(pnIdx);
			int countIdx = dc.getColumnIndex(AlertTable.COUNTDOWN);
			int countDown = dc.getInt(countIdx);
			countdowns.put(packageName, countDown);
		}
		return countdowns;
	}

}
