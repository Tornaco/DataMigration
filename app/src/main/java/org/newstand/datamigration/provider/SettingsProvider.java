package org.newstand.datamigration.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.annotation.StringRes;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.strategy.WorkMode;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import lombok.NonNull;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SettingsProvider extends Observable {

    private static final String KEY_AUTO_CONNECT_ENABLED = "key_auto_connect_enabled";
    private static final String KEY_DEVICE_NAME = "key_dev_name";
    private static final String KEY_TRANSITION_ANIMATION = "key_transition_animation";
    private static final String KEY_SERVER_PORTS = "key_server_ports";
    private static final String KEY_WORK_MODE = "key_work_mode";
    private static final String KEY_DEBUG_ENABLED = "key_debug_mode";
    private static final String KEY_DEF_SMS_APP = "key_def_sms_app";
    private static final String KEY_BACKUP_INTERVAL = "key_backup_interval";
    private static final String KEY_USER_NOTICED = "key_user_noticed";

    private static final String APP_DATA_DIR = "data/data";

    private static final String BACKUP_DATA_DIR_NAME = "data";
    private static final String BACKUP_APK_DIR_NAME = "apk";
    private static final String BACKUP_SESSION_INFO_FILE_NAME = "session.info";

    private static final String LICENSE_ROOT_DIR = "license";

    private static SettingsProvider sMe;

    private SharedPreferences mSharedPreferences;
    private Resources mRes;

    public SettingsProvider(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                setChanged();
                notifyObservers(key);
            }
        });
        mRes = context.getResources();
    }

    public static void init(Context context) {
        sMe = new SettingsProvider(context);
    }

    public boolean readBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public boolean readBoolean(String key, @BoolRes int defValue) {
        return mSharedPreferences.getBoolean(key, mRes.getBoolean(defValue));
    }

    public void writeBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String readString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public String readString(String key, @StringRes int defValue) {
        return mSharedPreferences.getString(key, mRes.getString(defValue));
    }

    public void writeString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    private static final String COMMON_BACKUP_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + ".DataMigration"
            + File.separator
            + "Backup";

    private static final String COMMON_RECEIVED_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + ".DataMigration"
            + File.separator
            + "Received";

    private static final String LOG_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + ".DataMigration"
            + File.separator
            + "Logs";

    private static final String TEST_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + ".DataMigration"
            + File.separator
            + "Test";

    public static String getBackupRootDir() {
        return COMMON_BACKUP_DIR;
    }

    public static String getReceivedRootDir() {
        return COMMON_RECEIVED_DIR;
    }

    public static String getBackupSessionAssetFile() {
        return COMMON_BACKUP_DIR
                + File.separator
                + "backup_sessions_assets.realm";
    }

    public static String getBackupSessionDir(Session session) {
        return COMMON_BACKUP_DIR + File.separator + session.getName();
    }

    // .DM/Backup/XXXX-XX/session.info
    public static String getBackupSessionInfoPath(Session session) {
        return getBackupSessionDir(session) + File.separator + BACKUP_SESSION_INFO_FILE_NAME;
    }

    public static String getBackupSessionInfoFileName() {
        return BACKUP_SESSION_INFO_FILE_NAME;
    }

    public static String getRecSessionDir(Session session) {
        return COMMON_RECEIVED_DIR + File.separator + session.getName();
    }

    public static String getLogDir() {
        return LOG_DIR;
    }

    public static String getTestDir() {
        return TEST_DIR;
    }

    public static String getBackupDirByCategory(DataCategory category, Session session) {
        return COMMON_BACKUP_DIR
                + File.separator
                + session.getName()
                + File.separator
                + category.name();
    }

    public static String getReceivedDirByCategory(DataCategory category, Session session) {
        return COMMON_RECEIVED_DIR
                + File.separator
                + session.getName()
                + File.separator
                + category.name();
    }

    public static String getRestoreDirByCategory(DataCategory category, Session session) {
        switch (category) {
            case Music:
                return Environment.getExternalStorageDirectory().getPath()
                        + File.separator
                        + Environment.DIRECTORY_MUSIC;
            case Photo:
                return Environment.getExternalStorageDirectory().getPath()
                        + File.separator
                        + Environment.DIRECTORY_PICTURES;
            case Video:
                return Environment.getExternalStorageDirectory().getPath()
                        + File.separator
                        + Environment.DIRECTORY_MOVIES;
            case CustomFile:
                return Environment.getExternalStorageDirectory().getPath()
                        + File.separator
                        + category.name()
                        + File.separator
                        + session.getName();

            default:
                throw new IllegalArgumentException("Unknown for:" + category.name());
        }
    }

    public static int[] getTransportServerPorts() {
        String str = sMe.readString(KEY_SERVER_PORTS, R.string.def_transport_server_ports);
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        int N = stringTokenizer.countTokens();
        int[] ports = new int[N];
        for (int i = 0; i < N; i++) {
            int p = Integer.parseInt(stringTokenizer.nextToken());
            ports[i] = p;
        }
        return ports;
    }

    public static String getWFDDeviceNamePrefix() {
        return "DM_SERIAL_";
    }

    public static String getDeviceName() {
        String def = getWFDDeviceNamePrefix() + Build.DEVICE + "@" + Build.SERIAL;
        return sMe.readString(KEY_DEVICE_NAME, def);
    }

    public static void setDeviceName(@NonNull String name) {
        sMe.writeString(KEY_DEVICE_NAME, name);
    }

    public static boolean isAutoConnectEnabled() {
        return sMe.readBoolean(KEY_AUTO_CONNECT_ENABLED, false);
    }

    public static void setAutoConnectEnabled(boolean value) {
        sMe.writeBoolean(KEY_AUTO_CONNECT_ENABLED, value);
    }

    public static boolean isTransitionAnimationEnabled() {
        return sMe.readBoolean(KEY_TRANSITION_ANIMATION, R.bool.def_transition_animation_enabled);
    }

    public static void setTransitionAnimationEnabled(boolean value) {
        sMe.writeBoolean(KEY_TRANSITION_ANIMATION, value);
    }

    public static WorkMode getWorkMode() {
        return WorkMode.valueOf(sMe.readString(KEY_WORK_MODE, WorkMode.NORMAL.name()));
    }

    public static void setWorkMode(WorkMode mode) {
        sMe.writeString(KEY_WORK_MODE, mode.name());
    }

    public static long getDiscoveryTimeout() {
        return 60 * 1000;
    }

    public static long getRequestConnectioninfoTimeout() {
        return 12 * 1000;
    }

    public static void observe(Observer observer) {
        sMe.addObserver(observer);
    }

    public static void unObserve(Observer observer) {
        sMe.deleteObserver(observer);
    }

    public static String getAppDataDir() {
        return APP_DATA_DIR;
    }

    public static String getBackupAppDataDirName() {
        return BACKUP_DATA_DIR_NAME;
    }

    public static String getBackupAppApkDirName() {
        return BACKUP_APK_DIR_NAME;
    }

    public static String getLicenseRootDir() {
        return LICENSE_ROOT_DIR;
    }

    public static void setDebugEnabled(boolean enabled) {
        sMe.writeBoolean(KEY_DEBUG_ENABLED, enabled);
    }

    public static boolean isDebugEnabled() {
        return sMe.readBoolean(KEY_DEBUG_ENABLED, R.bool.def_debug_enabled);
    }

    public static String getDefSmsApp() {
        return sMe.readString(KEY_DEF_SMS_APP, "com.android.mms");
    }

    public static void setDefSmsApp(String pkgName) {
        sMe.writeString(KEY_DEF_SMS_APP, pkgName);
    }

    public static Interval getBackupInterval() {
        return Interval.valueOf(sMe.readString(KEY_BACKUP_INTERVAL, Interval.Minutes.name()));
    }

    public static void setBackupInterval(Interval interval) {
        sMe.writeString(KEY_BACKUP_INTERVAL, interval.name());
    }

    public static boolean isUserNoticed() {
        return sMe.readBoolean(KEY_USER_NOTICED, false);
    }

    public static void setUserNoticed(boolean noticed) {
        sMe.writeBoolean(KEY_USER_NOTICED, noticed);
    }
}
