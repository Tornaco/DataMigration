package org.newstand.datamigration.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;

import lombok.NonNull;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SettingsProvider {

    private static final String KEY_AUTO_CONNECT_ENABLED = "key_auto_connect_enabled";
    private static final String KEY_DEVICE_NAME = "key_dev_name";

    private static SettingsProvider sMe;

    private SharedPreferences mSharedPreferences;

    public SettingsProvider(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        sMe = new SettingsProvider(context);
    }

    public boolean readBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void writeBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String readString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public void writeString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    private static final String COMMON_BACKUP_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + ".DataMigration"
            + File.separator
            + "Backup";

    public static String getBackupRootDir() {
        return COMMON_BACKUP_DIR;
    }

    public static String getBackupDirByCategory(DataCategory category, Session session) {
        return COMMON_BACKUP_DIR
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

            default:
                throw new IllegalArgumentException("Unknown for:" + category.name());
        }
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

    public static boolean autoConnectEnabled() {
        return sMe.readBoolean(KEY_AUTO_CONNECT_ENABLED, false);
    }

    public static void setAutoConnectEnabled(boolean value) {
        sMe.writeBoolean(KEY_AUTO_CONNECT_ENABLED, value);
    }

    public static long getDiscoveryTimeout() {
        return 60 * 1000;
    }

    public static long getRequestConnectioninfoTimeout() {
        return 12 * 1000;
    }
}
