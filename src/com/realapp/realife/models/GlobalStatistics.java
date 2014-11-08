package com.realapp.realife.models;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

import com.realapp.realife.models.apps.AppStatistics;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.StringFormat;

public class GlobalStatistics {

	private static GlobalStatistics context = new GlobalStatistics();
	
	private List<WeakReference<TextView>> totalTimeTextToUpdate =  new ArrayList<WeakReference<TextView>>();


	private AppStatistics globalStatistics;
	
	public static GlobalStatistics getInstance() {
		return context;
	}


	public AppStatistics getGlobalStatistics() {
		if(globalStatistics==null){
			globalStatistics = new AppStatistics(new ArrayList<IAppItem>());
		}
		return globalStatistics;
	}

	public void setGlobalStatistics(AppStatistics globalStatistics) {
		this.globalStatistics = globalStatistics;
	}
	
	public void addTotalTimeTextToUpdate(TextView textView){
		totalTimeTextToUpdate.add(new WeakReference<TextView>(textView));
	}
	
	public void updateAllTotalTimeText(){
		for(WeakReference<TextView> totalTimeTextViewRef : totalTimeTextToUpdate){
			if(totalTimeTextViewRef.get()!=null){
				TextView totalTimeTextView = totalTimeTextViewRef.get();
				int totalActiveTime = GlobalStatistics.getInstance().getGlobalStatistics().getTotalActiveTime();
				totalTimeTextView.setText(StringFormat.getFormattedGlobalTimeString(totalActiveTime));
			}
		}
	}


	

	
	

}
