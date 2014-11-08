package com.realapp.realife.models.apps;

public class AppID {
	
	private String appName;
	private String packageName;
	private int categoryID;
	
	public AppID(String appName, String packageName, int categoryID){
		this.appName = appName;
		this.packageName = packageName;
		this.categoryID = categoryID;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

}
