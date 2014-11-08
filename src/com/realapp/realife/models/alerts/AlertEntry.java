package com.realapp.realife.models.alerts;

public class AlertEntry {

	public String packageName;
	public int countDown;
	public int startCount;
	
	public AlertEntry(String packageName, int countDown, int startCount){
		this.packageName = packageName;
		this.countDown = countDown;
		this.startCount = startCount;
	}
}
