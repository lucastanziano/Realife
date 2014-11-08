package com.realapp.realife.models.apps;



public class AppUsageSample {

	private String packageName;
	private long hourDayTimeMillis;
	private int activeTime;
	private int userChecks;
    	


	public AppUsageSample(String packageName, long hourDayTimeMillis, int activeTime, int userChecks) {
		setPackageName(packageName);
		setHourDayTimeMillis(hourDayTimeMillis);
	    setActiveTime(activeTime);
	    setUserChecks(userChecks);
	}

	
	public void incrementActiveTime(){
		this.activeTime++;
	}
	
	public void incrementUserChecks(){
		this.userChecks++;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getHourDayTimeMillis() {
		return hourDayTimeMillis;
	}

	public void setHourDayTimeMillis(long hourDayTimeMillis) {
		this.hourDayTimeMillis = hourDayTimeMillis;
	}

	public int getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
	}

	public int getAppUses() {
		return userChecks;
	}

	public void setUserChecks(int userChecks) {
		this.userChecks = userChecks;
	}

	
}
