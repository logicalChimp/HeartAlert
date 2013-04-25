package uk.co.mentalspace.android.heartalert;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class Preferences implements OnSharedPreferenceChangeListener {

	private static final String SETTING_AUTO_CONNECT = "setting_auto_connect";
	private static final String SETTING_SHOW_CONNECTION_NOTIFICATION = "setting_show_connection_notification";
	private static final String SETTING_SHOW_HR_IN_NOTIFICATION = "setting_show_hr_in_notification";
	private static final String SETTING_MIN_ALARM_ENABLED = "setting_min_alarm_enabled";
	private static final String SETTING_MIN_ALARM_VALUE = "setting_min_alarm_value";
	private static final String SETTING_MAX_ALARM_ENABLED = "setting_max_alarm_enabled";
	private static final String SETTING_MAX_ALARM_VALUE = "setting_max_alarm_value";
	private static final String SETTING_DEBUG_ENABLE_ERROR_LOGGING = "setting_debug_enable_error_logging";
	private static final String SETTING_DEBUG_ENABLE_WARNING_LOGGING = "setting_debug_enable_warning_logging";
	private static final String SETTING_DEBUG_ENABLE_INFO_LOGGING = "setting_debug_enable_info_logging";
	private static final String SETTING_DEBUG_ENABLE_DEBUG_LOGGING = "setting_debug_enable_debug_logging";
	private static final String SETTING_DEBUG_ENABLE_VERBOSE_LOGGING = "setting_debug_enable_verbose_logging";

	public static boolean autoConnect = false;
	public static boolean showConnectionNotification = true;
	public static boolean showHRInNotification = true;
	public static boolean minAlarmEnabled = false;
	public static int minAlarmValue = 50;
	public static boolean maxAlarmEnabled = true;
	public static int maxAlarmValue = 180;

	public static boolean enableErrorLogging = false;
	public static boolean enableWarningLogging = false;
	public static boolean enableInfoLogging = false;
	public static boolean enableDebugLogging = false;
	public static boolean enableVerboseLogging = false;
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
		loadSharedPreference(sharedPrefs, key);
	}
	
	public static void loadPreferences(SharedPreferences sharedPrefs) {
		loadSharedPreference(sharedPrefs, SETTING_AUTO_CONNECT);
		loadSharedPreference(sharedPrefs, SETTING_SHOW_CONNECTION_NOTIFICATION);
		loadSharedPreference(sharedPrefs, SETTING_SHOW_HR_IN_NOTIFICATION);
		loadSharedPreference(sharedPrefs, SETTING_MIN_ALARM_ENABLED);
		loadSharedPreference(sharedPrefs, SETTING_MIN_ALARM_VALUE);
		loadSharedPreference(sharedPrefs, SETTING_MAX_ALARM_ENABLED);
		loadSharedPreference(sharedPrefs, SETTING_MAX_ALARM_VALUE);
		loadSharedPreference(sharedPrefs, SETTING_DEBUG_ENABLE_ERROR_LOGGING);
		loadSharedPreference(sharedPrefs, SETTING_DEBUG_ENABLE_WARNING_LOGGING);
		loadSharedPreference(sharedPrefs, SETTING_DEBUG_ENABLE_INFO_LOGGING);
		loadSharedPreference(sharedPrefs, SETTING_DEBUG_ENABLE_DEBUG_LOGGING);
		loadSharedPreference(sharedPrefs, SETTING_DEBUG_ENABLE_VERBOSE_LOGGING);
	}
	
	private static void loadSharedPreference(SharedPreferences sharedPrefs, String key) {
		if (SETTING_AUTO_CONNECT.equals(key)) {
			autoConnect = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_SHOW_CONNECTION_NOTIFICATION.equals(key)) {
			showConnectionNotification = sharedPrefs.getBoolean(key, true);
		}
		if (SETTING_SHOW_HR_IN_NOTIFICATION.equals(key)) {
			showHRInNotification = sharedPrefs.getBoolean(key, true);
		}
		if (SETTING_MIN_ALARM_ENABLED.equals(key)) {
			minAlarmEnabled = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_MIN_ALARM_VALUE.equals(key)) {
			minAlarmValue = Integer.valueOf(sharedPrefs.getString(key, "50"));
		}
		if (SETTING_MAX_ALARM_ENABLED.equals(key)) {
			maxAlarmEnabled = sharedPrefs.getBoolean(key, true);
		}
		if (SETTING_MAX_ALARM_VALUE.equals(key)) {
			maxAlarmValue = Integer.valueOf(sharedPrefs.getString(key, "180"));
		}
		if (SETTING_DEBUG_ENABLE_ERROR_LOGGING.equals(key)) {
			enableErrorLogging = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_DEBUG_ENABLE_WARNING_LOGGING.equals(key)) {
			enableWarningLogging = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_DEBUG_ENABLE_INFO_LOGGING.equals(key)) {
			enableInfoLogging = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_DEBUG_ENABLE_DEBUG_LOGGING.equals(key)) {
			enableDebugLogging = sharedPrefs.getBoolean(key, false);
		}
		if (SETTING_DEBUG_ENABLE_VERBOSE_LOGGING.equals(key)) {
			enableVerboseLogging = sharedPrefs.getBoolean(key, false);
		}
	}

}
