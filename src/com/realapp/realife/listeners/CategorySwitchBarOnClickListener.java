package com.realapp.realife.listeners;

import com.realapp.realife.fragments.AddictionMeasurerFragment;
import com.realapp.realife.util.HandlerNotifier;

import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class CategorySwitchBarOnClickListener implements OnClickListener{
	
	private Handler handler;
	private int categoryPageId;

	public CategorySwitchBarOnClickListener(Handler handler, int categoryPageId)
	{
		this.handler = handler;
		this.categoryPageId = categoryPageId;
	}
	@Override
	public void onClick(View v) {
		HandlerNotifier.notifyIntMessage(handler, AddictionMeasurerFragment.BTN_PRESSED_TAG, categoryPageId);		
	}

}
