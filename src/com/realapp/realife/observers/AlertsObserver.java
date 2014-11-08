package com.realapp.realife.observers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.realapp.realife.ReaLifeApplication;
import com.realapp.realife.loaders.AppUsageLoader;
import com.realapp.realife.service.MonitorService;

public class AlertsObserver extends BroadcastReceiver{

	private MonitorService mService;
	
	public AlertsObserver(MonitorService mService){
		this.mService = mService;
		
		// Register for events related to application installs/removals/updates.
	    IntentFilter filter = new IntentFilter(ReaLifeApplication.ACTION_ALERT_CHANGED);
	    mService.getApplicationContext().registerReceiver(this, filter);
	}
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		    // Tell the loader about the change.
		mService.loadAppAlerts();
		
	}

}
