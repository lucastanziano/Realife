package com.realapp.realife.util;

import java.util.Calendar;

public class TimeUtil {
	
	public static int getHourByTimeMillisec(long timeMillisec){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMillisec);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	
	/**
	 * @return the current time in millisec rounded down by hour
	 */
	public static long getCurrentHourDayTimeMillis() {
		Calendar hourDay = Calendar.getInstance();

		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		hourDay.set(year, month, day, hour, 0, 0);
		
		return (hourDay.getTimeInMillis()/1000)*1000;
	}
	
	
	public static long getTodayMidnightDateMillis() {
		Calendar today = Calendar.getInstance();

		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		today.set(year, month, day, 0, 0, 0);
		return today.getTimeInMillis()-10000;
	}

	public static long getMidnightDateMillis(long dateMillis) {
		Calendar date = Calendar.getInstance();
        date.setTimeInMillis(dateMillis);
        
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		date.set(year, month, day, 0, 0, 0);
		return (date.getTimeInMillis()/1000)*1000;
	}
	
	
	

}
