package uk.co.mentalspace.android.heartalert;

import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {
	private static final String LOGNAME = "SettingsActivity";
	
	private static final String CONNECT_BUTTON_NOT_CONNECTED_TITLE = "Start the service";
	private BroadcastReceiver connStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	SettingsActivity.this.receiveBroadcast(intent);
        }
    };
    private boolean receiverIsRegistered = false;
	private Preferences prefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		prefs = new Preferences();
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(prefs);

		Intent request = new Intent(this, MonitorService.class);
		request.setAction(MonitorService.ACTION_REQUEST_CONNECTION_STATUS_UPDATE);
		startService(request);
		
		Preference connect = findPreference("setting_start_service");
		connect.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference arg0) {
				if (CONNECT_BUTTON_NOT_CONNECTED_TITLE.equals(arg0.getTitle())) {
					if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending 'Request connection' intent");
					Intent intent = new Intent(SettingsActivity.this, MonitorService.class);
					intent.setAction(MonitorService.ACTION_CONNECT_TO_HXM);
					SettingsActivity.this.startService(intent);
				} else {
					if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending 'Request disconnection' intent");
					Intent intent = new Intent(SettingsActivity.this, MonitorService.class);
					intent.setAction(MonitorService.ACTION_DISCONNECT);
					SettingsActivity.this.startService(intent);
				}
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!receiverIsRegistered) {
		    registerReceiver(connStatusReceiver, new IntentFilter(MonitorService.ACTION_CONNECTION_STATUS_RESPONSE));
		    receiverIsRegistered = true;
		}
	}
	
	@Override
	public void onPause() {
    	if (receiverIsRegistered) {
		    unregisterReceiver(connStatusReceiver);
		    receiverIsRegistered = false;
    	}
    	super.onPause();
	}

    public void receiveBroadcast(Intent intent) {
    	if (MonitorService.ACTION_CONNECTION_STATUS_RESPONSE.equals(intent.getAction())) {
    		int status = intent.getIntExtra(MonitorService.EXTRA_STATUS, MonitorService.STATUS_NOT_CONNECTED);
//    		boolean isConnected = intent.getBooleanExtra(MonitorService2.EXTRA_STATUS, false);
    		boolean isConnected = MonitorService.STATUS_CONNECTED == status;

			Preference connect = findPreference("setting_start_service");
    		
    		String prefTitle = CONNECT_BUTTON_NOT_CONNECTED_TITLE;
			String prefSummary = "Start the monitoring service, if it is not already running";
			if (isConnected) {
				prefTitle = "Stop the service";
				prefSummary = "Disconnect from the HxM and stop the service";
			} else if (MonitorService.STATUS_NO_BLUETOOTH == status) {
				prefTitle = "No Bluetooth";
				prefSummary = "Bluetooth is not present on this device.";
				connect.setEnabled(false);
			}

			connect.setTitle(prefTitle);
			connect.setSummary(prefSummary);
    	}
    }
}
