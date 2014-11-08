package com.realapp.realife.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.realapp.realife.R;
import com.realapp.realife.models.GlobalStatistics;
import com.realapp.realife.util.StringFormat;
import com.realapp.realife.util.TimeUtil;

public class FancyBackgroundFragment extends SherlockFragment {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fancy_background, container,
				false);
		TextView totalTime = (TextView) rootView.findViewById(R.id.fancyBackTotalTimeValue);
		int totalActiveTime = GlobalStatistics.getInstance().getGlobalStatistics().getTotalActiveTime();
		totalTime.setText(StringFormat.getFormattedGlobalTimeString(totalActiveTime));
		GlobalStatistics.getInstance().addTotalTimeTextToUpdate(totalTime);
		return rootView;
	}
}
