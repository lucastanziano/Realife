package com.realapp.realife.models.apps;

import java.util.List;


public class AppStatistics {

	private int totalActiveTime;
	private int totalAppsCheck;
	private int totalDependencyPts;
	private int maxAddiction;

	public AppStatistics(List<IAppItem> apps) {

		totalActiveTime = 0;
		totalAppsCheck = 0;
		totalDependencyPts = 0;
		maxAddiction = 0;

		for (IAppItem appItem : apps) {
			AppUsageItem app = (AppUsageItem)appItem;
			totalActiveTime += app.getActiveTime();
			totalAppsCheck += app.getAppUses();
			totalDependencyPts += app.getAddiction();
			if(app.getAddiction() > maxAddiction){
				maxAddiction = (int) app.getAddiction();
			}
		}
		if (apps.size() > 0) {
			totalDependencyPts /= apps.size();
		}
	}

	public int getTotalDependencyPts() {
		return totalDependencyPts;
	}

	public void setTotalDependencyPts(int totalDependencyPts) {
		this.totalDependencyPts = totalDependencyPts;
	}

	public int getTotalAppsCheck() {
		return totalAppsCheck;
	}

	public void setTotalAppsCheck(int totalAppsCheck) {
		this.totalAppsCheck = totalAppsCheck;
	}

	public int getTotalActiveTime() {
		return totalActiveTime;
	}

	public void setTotalActiveTime(int totalActiveTime) {
		this.totalActiveTime = totalActiveTime;
	}

	public int getMaxAddiction() {
		return maxAddiction;
	}

	public void setMaxAddiction(int maxAddiction) {
		this.maxAddiction = maxAddiction;
	}

}
