package com.realapp.realife.models.apps;

import android.graphics.drawable.Drawable;


public class AppIDItem implements IAppItem {

	private Drawable icon;
	private AppID appID;
	private boolean checked;
	
	public AppIDItem(Drawable icon, AppID appID){
		this.icon = icon;
		this.appID = appID;
		this.checked = false;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public AppID getAppID() {
		return appID;
	}

	public void setAppID(AppID app) {
		this.appID = app;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
