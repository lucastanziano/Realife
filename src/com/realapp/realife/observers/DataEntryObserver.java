package com.realapp.realife.observers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.realapp.realife.ReaLifeApplication;
import com.realapp.realife.loaders.AppUsageLoader;
import com.realapp.realife.loaders.BaseLoader;
import com.realapp.realife.models.GlobalStatistics;

public class DataEntryObserver extends BroadcastReceiver {

	private BaseLoader dLoader;

	public DataEntryObserver() {

	}

	public void setAppItemLoader(BaseLoader baseLoader) {
		this.dLoader = baseLoader;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (dLoader != null) {
			// Tell the loader about the change.
			dLoader.onContentChanged();
		}
	}

}
