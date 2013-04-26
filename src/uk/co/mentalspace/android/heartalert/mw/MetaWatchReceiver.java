package uk.co.mentalspace.android.heartalert.mw;

import uk.co.mentalspace.android.heartalert.MonitorService;
import android.content.BroadcastReceiver;
import uk.co.mentalspace.android.heartalert.Preferences;
import uk.co.mentalspace.android.heartalert.R;
import uk.co.mentalspace.android.mw.utils.MW;
import uk.co.mentalspace.android.mw.utils.MWIntentGenerator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MetaWatchReceiver extends BroadcastReceiver implements MW {
	private static final String LOGNAME = "MetaWatchReceiver";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		try {
			final String action = intent.getAction();
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Received Intent.  Action: " + action);
	
			if (ACTION_DISCOVERY.equals(action)) {
				String appId = ctx.getResources().getString(R.string.app_id);
				String appName = ctx.getResources().getString(R.string.app_name);
				Intent announce = MWIntentGenerator.getAnnounceIntent(ctx, appId, appName);
				if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending announce intent, id ["+appId+"], name ["+appName+"]");
				ctx.sendBroadcast(announce);
				return;
			}

			else if (ACTION_ACTIVATE.equals(action) || 
					 ACTION_DEACTIVATE.equals(action) || 
					 MonitorService.ACTION_HXM_READING.equals(action)) {

				Intent service = new Intent(ctx, MetaWatchService.class);
				service.setAction(action);
				service.putExtras(intent.getExtras());
				ctx.startService(service);
			}
			else {
				if (Preferences.enableWarningLogging) Log.w(LOGNAME, "Unrecognised intent action: "+action);
			}
		} catch (Exception e) {
			if (Preferences.enableErrorLogging) Log.e(LOGNAME, "Unexpected exception", e);
		}
	}
}
