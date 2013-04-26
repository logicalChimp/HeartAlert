package uk.co.mentalspace.android.heartalert.mw;


import java.util.Date;
import uk.co.mentalspace.android.heartalert.HxmReading;
import uk.co.mentalspace.android.heartalert.MonitorService;
import uk.co.mentalspace.android.heartalert.Preferences;
import uk.co.mentalspace.android.heartalert.R;
import uk.co.mentalspace.android.mw.utils.Utils;
import uk.co.mentalspace.android.mw.utils.MW;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;

public class MetaWatchService extends IntentService implements MW {
	private static final String LOGNAME = "MetaWatchService";
	
	private static final long THIRTY_SECONDS = 30*1000;
	
	private static long lastMinVibrate = 0;
	private static long lastMaxVibrate = 0;

	private static boolean isActive = false;
	private static boolean invert = false;
	private static boolean lastInverted = false;
	private static Typeface fontface;
	private static int fontsize = 8; //8pt;

	public MetaWatchService() {
		super("MetaWatchService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (null == intent) return;
		
		String action = intent.getAction();
		if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Received intent action ["+action+"]");
		
		if (MonitorService.ACTION_HXM_READING.equals(action)) {
			HxmReading reading = (HxmReading)intent.getSerializableExtra(MonitorService.EXTRA_READING);
			handleReading(reading);
			return;
		}

		final String appId = intent.getStringExtra("id");
		if (!getResources().getString(R.string.app_id).equals(appId)) {
			if (Preferences.enableInfoLogging) Log.i(LOGNAME, "Intent intended for app ["+appId+"], not me - ignoring");
			return;
		}
		
		if (ACTION_ACTIVATE.equals(action)) {
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "setting isActive to true");
			isActive = true;
			return;
		}
		if (ACTION_DEACTIVATE.equals(action)) {
			isActive = false;
			return;
		}
	}

	public void handleReading(HxmReading reading) {
		if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Send update to watch? "+isActive);
		if (isActive) sendUpdateToWatch(reading);
		
		if (Preferences.minAlarmEnabled) {
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Min alarm enabled - testing");
			checkForMinAlarm(reading);
		}
		if (Preferences.maxAlarmEnabled) {
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Max alarm enabled - testing");
			checkForMaxAlarm(reading);
		}
	}
	
	private void checkForMinAlarm(HxmReading reading) {
		if (isUnderMinThreshold(reading)) {
			long now = (new Date()).getTime();
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "HR Low - Sound min alarm? last vibrate ["+(lastMinVibrate+THIRTY_SECONDS)+"], now ["+now+"]");
			if ((lastMinVibrate+THIRTY_SECONDS) < now) {
				if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending vibration alarm...");
				lastMinVibrate = now;
				Intent intent = new Intent(ACTION_VIBRATE);
				intent.putExtra(EXTRA_VIBRATE_ON, 800);
				intent.putExtra(EXTRA_VIBRATE_OFF, 200);
				intent.putExtra(EXTRA_VIBRATE_CYCLES, 3);
				sendBroadcast(intent);
			}
		}
	}
	
	private void checkForMaxAlarm(HxmReading reading) {
		if (isOverMaxThreshold(reading)) {
			long now = (new Date()).getTime();
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "HR High - Sound max alarm? last vibrate ["+(lastMaxVibrate+THIRTY_SECONDS)+"], now ["+now+"]");
			if ((lastMaxVibrate+THIRTY_SECONDS) < now) {
				if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending vibration alarm...");
				lastMaxVibrate = now;
				Intent intent = new Intent(ACTION_VIBRATE);
				intent.putExtra(EXTRA_VIBRATE_ON, 100);
				intent.putExtra(EXTRA_VIBRATE_OFF, 200);
				intent.putExtra(EXTRA_VIBRATE_CYCLES, 10);
				sendBroadcast(intent);
			}
		}
	}
	
	private boolean invertThisUpdate(HxmReading reading) {
		boolean outsideThreshold = isUnderMinThreshold(reading) || isOverMaxThreshold(reading);
		if (!outsideThreshold) return invert;
		
		lastInverted = !lastInverted;
		return lastInverted;
	}
	
	private boolean isUnderMinThreshold(HxmReading reading) {
		int minHR = Preferences.minAlarmValue;
		int curHR = reading.heartRate;
		return (curHR < minHR);
	}
	
	private boolean isOverMaxThreshold(HxmReading reading) {
		int maxHR = Preferences.maxAlarmValue;
		int curHR = reading.heartRate;
		return (curHR > maxHR);
	}
	
	private void sendUpdateToWatch(HxmReading reading) {
		if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending update to watch...");
		Bitmap bitmap = Bitmap.createBitmap(METAWATCH_WIDTH, METAWATCH_HEIGHT, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		
		if (null == fontface) {
			if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Fontface not yet initialised - loading");
			fontface = Typeface.createFromAsset(getAssets(), "metawatch_8pt_7pxl_CAPS.ttf");
		}

		//set background color of the canvas
		boolean invertThisUpdate = invertThisUpdate(reading);
		int canvasColor = ((invertThisUpdate) ? Color.BLACK : Color.WHITE);
		int textColor = (invertThisUpdate) ? Color.WHITE : Color.BLACK;
		canvas.drawColor(canvasColor);

		//create default textpaint for drawing on canvas
		TextPaint tp = new TextPaint();
		tp.setColor(textColor);
		tp.setTypeface(fontface);
		tp.setTextSize(fontsize);
		tp.setTextAlign(Align.LEFT);

		canvas.drawText("Zephyr", 2, 10, tp);
		canvas.drawText("Heart rate:  "+reading.heartRate, 2, 25, tp);
		canvas.drawText("Beat number: "+reading.heartBeatNumber, 2, 35, tp);
		canvas.drawText("Speed:       "+reading.speed, 2, 45, tp);
		canvas.drawText("Strides      "+reading.strides, 2, 55, tp);

		int[] array = Utils.bitmapToPixelArray(bitmap);
		
		if (Preferences.enableDebugLogging) Log.d(LOGNAME, "Sending Update intent");
		Intent intent = new Intent(ACTION_UPDATE);
		Bundle b = new Bundle();
		b.putString(EXTRA_APP_ID, getResources().getString(R.string.app_id));
		b.putIntArray(EXTRA_INT_ARRAY, array);
		intent.putExtras(b);
		intent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		sendBroadcast(intent);
	}
}
