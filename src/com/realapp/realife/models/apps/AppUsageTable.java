package com.realapp.realife.models.apps;

public class AppUsageTable {


	public static final String TABLE_NAME = "data_table";
	public static String _ID = "ID";
	public static final String ACTIVE_TIME = "ActiveTime";
	public static final String USER_CHECKS = "UserChecks";
	public static final String PACKAGE_NAME = "PackageName";
	public static final String HOUR_DAY_TIMEMILLIS = "HourDayTimeMillis";
	
	
	public static String[] COLUMNS = new String[]
			{ _ID, HOUR_DAY_TIMEMILLIS, PACKAGE_NAME, ACTIVE_TIME, USER_CHECKS };

}
