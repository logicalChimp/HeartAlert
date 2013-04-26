package uk.co.mentalspace.android.heartalert;

import java.util.Set;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MonitorService extends Service {
	private static final String LOGNAME = "MonitorService";
	private static final String HXM_ID_START = "HXM";
	
	private HxmService hxmService = null;

	public static final String NOTIFICATION_TITLE_HXM_CONNECTED = "HxM Connected";
	public static final String NOTIFICATION_TITLE_HXM_DISCONNECTED = "HxM Disconnected";
	
	public static final String ACTION_CONNECT_TO_HXM = "uk.co.mentalspace.heartalert.connectToHxm";
	public static final String ACTION_DISCONNECT = "uk.co.mentalspace.heartalert.disconnect";
	public static final String ACTION_REQUEST_CONNECTION_STATUS_UPDATE = "uk.co.mentalspace.heartalert.requestConnectionStatus";
	public static final String ACTION_CONNECTION_STATUS_RESPONSE = "uk.co.mentalspace.heartalert.connectionStatusResponse";	
	public static final String ACTION_HXM_READING = "uk.co.mentalspace.heartalert.hxmReading";

	public static final String EXTRA_STATUS = "connStatus";
	public static final String EXTRA_READING = "hxmReading";

	public static final int STATUS_NOT_CONNECTED = 0;
	public static final int STATUS_CONNECTING = 1;
	public static final int STATUS_NONE_PAIRED = 2;
	public static final int STATUS_CONNECTED = 3;
	public static final int STATUS_NO_BLUETOOTH = 4;
	private int mStatus = STATUS_NOT_CONNECTED;

	private String mHxMName = null;
    private String mHxMAddress = null;
    private BluetoothAdapter mBluetoothAdapter = null;

	private int notificationId = 0;

	@Override
    public void onCreate() {
    	super.onCreate();
	    
	    //done once in onCreate - force Preferences to load the current Shared Preferences
	    Preferences.loadPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    @Override
    public void onDestroy() {
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Stopping HxM monitor service...");

    	if (hxmService != null) hxmService.stop();
    	hxmService = null;
    	
		setNotification(NOTIFICATION_TITLE_HXM_DISCONNECTED, "Application service terminated", android.R.drawable.ic_lock_idle_charging);
    	super.onDestroy();
    }

    public void handleIntent(Intent intent) {
    	if (null == intent) return;
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Handling intent. Action ["+intent.getAction()+"]");

    	if (ACTION_DISCONNECT.equals(intent.getAction())) {
    		stopSelf();
    	}
    	if (ACTION_CONNECT_TO_HXM.equals(intent.getAction())) {
	        connectToHxm();
    	}
    	if (ACTION_REQUEST_CONNECTION_STATUS_UPDATE.equals(intent.getAction())) {
    		sendConnectionStatusIntent();
    	}
	}

    protected void sendConnectionStatusIntent() {
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending connection status ["+mStatus+"] intent");
		Intent response = new Intent();
		response.setAction(ACTION_CONNECTION_STATUS_RESPONSE);
		response.putExtra(EXTRA_STATUS, mStatus);
		sendBroadcast(response);
    }
    
	private void handleReading(HxmReading reading) {
		int heartRate = reading.heartRate;
		if (Preferences.enableInfoLogging) Log.i(LOGNAME, "Heart rate: "+heartRate);
		setNotification(MonitorService.NOTIFICATION_TITLE_HXM_CONNECTED, "Current heart rate: "+heartRate, android.R.drawable.ic_lock_idle_charging);

		Intent intent = new Intent();
        intent.setAction(MonitorService.ACTION_HXM_READING);
        intent.putExtra(EXTRA_READING, reading);
        MonitorService.this.sendBroadcast(intent);
	}
	
    public void updateConnectionStatus(int statusCode) {
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Connection status changed.  from ["+mStatus+"] to ["+statusCode+"]");
    	mStatus = statusCode;
    	sendConnectionStatusIntent();
    }
    
	public void setNotification(String title, String msg, int drawable) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
	        .setSmallIcon(drawable)
	        .setContentTitle(title)
	        .setContentText(msg);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, SettingsActivity.class);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SettingsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(notificationId, mBuilder.build());	
	}
	
    /*
     * connectToHxm() sets up our service loops and starts the connection
     * logic to manage the HxM device data stream 
     */
    private void connectToHxm() {
    	// Update the status to connecting so the user can tell what's happening
      	mStatus = STATUS_CONNECTING;

      	if (null == mBluetoothAdapter) {
      		updateConnectionStatus(STATUS_NO_BLUETOOTH);
      		return;
      	}
      	
      	// Setup the service that will talk with the Hxm
	    if (hxmService == null) setupHrm();
	    
	    //Look for an Hxm to connect to, if none is found tell the user about it
	    if ( getFirstConnectedHxm() ) {
	    	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mHxMAddress);
	    	hxmService.connect(device); 	// Attempt to connect to the device
	    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Connection attempt running...");
	    } else {
	      	mStatus = STATUS_NONE_PAIRED;	    	
	    }    
    }
    
    /*
     * Loop through all the connected bluetooth devices, the first one that 
     * starts with HXM will be assumed to be our Zephyr HxM Heart Rate Monitor,
     * and this is the device we will connect to
     * 
     * returns true if a HxM is found and the global device address has been set 
     */
    private boolean getFirstConnectedHxm() {
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "getting details of first connected HxM device");

    	// Initialise the global device address to null, that means we haven't found a HxM to connect to yet    	
    	mHxMAddress = null;    	
    	mHxMName = null;
    	
	    // Get the local Bluetooth adapter
	    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

	    // Get a set of currently paired devices to cycle through, the Zephyr HxM must
	    // be paired to this Android device, and the bluetooth adapter must be enabled
	    Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();

	    // For each device check to see if it starts with HXM, if it does assume it
	    // is the Zephyr HxM device we want to pair with      
	    if (bondedDevices.size() > 0) {
	        for (BluetoothDevice device : bondedDevices) {
	        	String deviceName = device.getName();

	        	if ( deviceName.startsWith(HXM_ID_START) ) {
	        		// we found an HxM to try to talk to!, let's remember its name and stop looking for more
	        		mHxMAddress = device.getAddress();
	        		mHxMName = device.getName();
	        		if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Found a device whose name starts with ["+HXM_ID_START+"].\nName ["+mHxMName+"], Address ["+mHxMAddress+"]");
	        		break;
	        	}
	        }
	    }
    
	    // return true if we found an HxM and set the global device address
	    return (mHxMAddress != null);
   }

    private void setupHrm() {
    	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Instantiating HxmService");

        // Initialise the service to perform bluetooth connections
        hxmService = new HxmService(this, handler);
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int id) {
		handleIntent(intent);
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

    // The Handler that gets information back from the hrm service
	private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HxmService.MSG_TYPE_HXM_STATE: 
                int state = (Integer)msg.obj;
                if (Preferences.enableDebugLogging) Log.d(LOGNAME, "MSG Type [HXM STATE], Value ["+state+"]");
                switch (state) {
	                case HxmService.HXM_STATE_CONNECTED:
	                	MonitorService.this.updateConnectionStatus(MonitorService.STATUS_CONNECTED);
		                Toast.makeText(getApplicationContext(), "State change: now connected", Toast.LENGTH_SHORT).show();
	                    break;

	                case HxmService.HXM_STATE_CONNECTING:
	                	MonitorService.this.updateConnectionStatus(MonitorService.STATUS_CONNECTING);
		                Toast.makeText(getApplicationContext(), "State change: now connecting", Toast.LENGTH_SHORT).show();
	                    break;
	                    
	                case HxmService.HXM_STATE_RESTING:
	                	if (Preferences.enableDebugLogging) Log.d(LOGNAME, "MSG: Resting: delivery time ["+msg.getWhen()+"], msg ["+msg.toString()+"]");
	                	MonitorService.this.updateConnectionStatus(MonitorService.STATUS_NOT_CONNECTED);
		                Toast.makeText(getApplicationContext(), "State change: now resting", Toast.LENGTH_SHORT).show();
	                    break;
                }
                break;

            case HxmService.MSG_TYPE_HXM_READING: {
                if (Preferences.enableDebugLogging) Log.d(LOGNAME, "MSG Type [HXM READING]");
                byte[] readBuf = (byte[]) msg.obj;
                HxmReading hrm = new HxmReading( readBuf );
                MonitorService.this.handleReading(hrm);
                break;
            }
                
            case HxmService.MSG_TYPE_HXM_STATUS:
            	int msgId = msg.getData().getInt(HxmService.EXTRA_STATUS_CODE);
                if (Preferences.enableDebugLogging) Log.d(LOGNAME, "MSG Type [HXM STATUS], Value ["+msgId+"]");
            	switch (msgId) {
            	case HxmService.HXM_STATUS_CONNECT_TO_DEVICE_FAILED:
	                Toast.makeText(getApplicationContext(), "Connect to device failed", Toast.LENGTH_SHORT).show();
	                break;
            	case HxmService.HXM_STATUS_CONNECTION_LOST:
	                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
	                break;
            	case HxmService.HXM_STATUS_CONNECTED_TO_DEVICE:
            		String deviceName = msg.getData().getString(HxmService.EXTRA_DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to ["+deviceName+"]", Toast.LENGTH_SHORT).show();
	                break;
            	}
            }
        }
    };
}
