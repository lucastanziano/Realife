package com.realapp.realife.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.realapp.realife.util.Print;

public class BootCompletedReceiver extends BroadcastReceiver {

	  @Override
	  public void onReceive(Context context, Intent intent)
	  {
	      if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
	      {
	         Print.debug("BOOT COMPLETED: Starting the monitor service.");
             if( !MonitorService.isRunning(context)){
            	 context.startService(new Intent(context, MonitorService.class));
             }
	      }
	  }
	} 
