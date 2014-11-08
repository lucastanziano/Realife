package com.realapp.realife.util;

import com.realapp.realife.ReaLifeApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class Print {

	public final static String DEFAULT_ERROR = "ReaLife Error";
	public final static String DEFAULT_WARNING = "ReaLife Warning";
	public final static String DEFAULT_DEBUG = "ReaLife Debug";

	public static void error(String message, StackTraceElement[] stacktrace) {

		StringBuilder sb = new StringBuilder();
		for (StackTraceElement se : stacktrace) {
			sb.append(se);
			sb.append('\n');
		}
		Log.e(DEFAULT_ERROR, message + " Stack trace: " + sb.toString());
		System.exit(-1);
	}

//	public static void infoDialog(Context context, String title, String message) {
//		AlertDialog ad = new AlertDialog.Builder(context).create();
//		ad.setTitle(title);
//		ad.setMessage(message);
//		ad.show();
//	}

	public static void error(Throwable t) {
		Log.e(DEFAULT_ERROR, t.getMessage());
		StackTraceElement ste = t.getCause().getStackTrace()[0];
		Log.e(DEFAULT_ERROR, "Error in: " + ste.getClassName() + " at line #"
				+ ste.getLineNumber());
		for (StackTraceElement se : t.getCause().getStackTrace()) {
			Log.e(DEFAULT_ERROR, se.toString());
		}
		System.exit(-1);
		
	}

	public static void warning(String message) {
		if (ReaLifeApplication.debugMode) {
		Log.d(DEFAULT_WARNING, message);
		}
	}

	public static void debug(String message) {
		if (ReaLifeApplication.debugMode) {
			Log.d(DEFAULT_DEBUG, message);
		}
	}

}
