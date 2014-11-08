package com.realapp.realife.models.apps;

import android.graphics.drawable.Drawable;



public interface IAppItem {
	
	public abstract Drawable getIcon();

	public abstract void setIcon(Drawable icon);

	public abstract AppID getAppID();

	public abstract void setAppID(AppID app);

	public abstract boolean isChecked();

	public abstract void setChecked(boolean checked);
}
