package com.realapp.realife.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerNotifier {
	
	
	public static void notifyEmptyMessage(Handler handler, String str) {
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(str, "" + str);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	public static void notifyIntMessage(Handler handler, String tag, int arg) {
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(tag, arg);
		msg.setData(b);
		handler.sendMessage(msg);
	}

	public static void notifyStringMessage(Handler handler, String tag, String arg) {
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(tag, arg);
		msg.setData(b);
		handler.sendMessage(msg);
	}

}
