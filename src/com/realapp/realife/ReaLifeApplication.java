package com.realapp.realife;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
		formKey = "",
        formUri = "https://realife.cloudant.com/acra-realife/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="realife",
        formUriBasicAuthPassword="qazmlp1801",
        // Your usual ACRA configuration
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash_toast_text
        
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
        
)
public class ReaLifeApplication extends Application{
	
	public static final boolean debugMode = true;
	public static final String ACTION_DB_UPDATED =  "com.realapp.custom.intent.action.DBUPDATED";
	public static final String ACTION_ALERT_CHANGED = "com.realapp.realife.ACTION_ALERT_CHANGED";
	@Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
	
}
