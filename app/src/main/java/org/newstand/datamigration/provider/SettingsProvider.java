package org.newstand.datamigration.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.strategy.WorkMode;
import org.newstand.datamigration.worker.transport.Session;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SettingsProvider extends Observable {

    private static final String KEY_AUTO_CONNECT_ENABLED = "key_auto_connect_enabled";
    private static final String KEY_DEVICE_NAME = "key_dev_name";
    private static final String KEY_TRANSITION_ANIMATION = "key_transition_animation";
    private static final String KEY_CHANGE_LAUNCHER_ICON = "key_change_launcher_icon";
    private static final String KEY_SERVER_PORTS = "key_server_ports";
    private static final String KEY_WORK_MODE = "key_work_mode";
    private static final String KEY_DEBUG_ENABLED = "key_debug_mode";
    private static final String KEY_DEF_SMS_APP = "key_def_sms_app";
    private static final String KEY_BACKUP_INTERVAL = "key_backup_interval";
    private static final String KEY_USER_NOTICED = "key_user_noticed";
    private static final String KEY_APP_INTRO_NOTICED = "key_app_intro_noticed";
    private static final String KEY_DONATE_QR_PATH = "key_donate_qr_path";
    private static final String KEY_LAST_UPDATE_CHECK_TIME = "key_last_update_check_time";
    private static final String KEY_TIPS_NOTICED_PREFIX = "key_tips_noticed_";
    private static final String KEY_AUTO_BUG_REPORT = "key_bug_report";
    private static final String KEY_AUTO_INSTALL_APP = "key_auto_install_app";
    private static final String KEY_INSTALL_DATA = "key_install_data";
    private static final String KEY_APP_THEME_COLOR = "key_app_theme_color";
    private static final String KEY_ENCRYPT_ENABLED = "key_encrypt_enabled";
    private static final String KEY_DATA_MIGRATION_ROOT_DIR = "key_data_migration_root_dir";
    private static final String KEY_SHOW_AD = "key_show_ad";
    private static final String KEY_AD_PRESENT_TIMES = "key_ad_present_times";
    private static final String KEY_LOADER_CONFIG_CATEGORY_ENABLED_PREFIX = "loader_config_should_load_";
    private static final String KEY_APP_INSTALLER_TIMEOUT = "app_installer_timeout";

    private static final String APP_DATA_DIR = "data/data";

    private static final String BACKUP_DATA_DIR_NAME = "data";
    private static final String BACKUP_EXTRA_DATA_DIR_NAME = "extra_data";
    private static final String BACKUP_APK_DIR_NAME = "apk";
    private static final String BACKUP_SESSION_INFO_FILE_NAME = "session.info";
    private static final String BACKUP_SYSTEM_INFO_FILE_NAME = "system.info";

    private static final String LICENSE_ROOT_DIR = "license";

    private static final String WIFI_CONFIG_FILE_PATH = "data/misc/wifi/wpa_supplicant.conf";

    @Getter
    @Setter
    private static boolean isUnderTest;

    private static SettingsProvider sMe;

    private SharedPreferences mSharedPreferences;
    private Resources mRes;

    public SettingsProvider(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mRes = context.getResources();
    }

    public static void init(Context context) {
        sMe = new SettingsProvider(context);
    }

    private boolean readBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    private boolean readBoolean(String key, @BoolRes int defValue) {
        return mSharedPreferences.getBoolean(key, mRes.getBoolean(defValue));
    }

    private void writeBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
        setChanged();
        notifyObservers();
    }

    public String readString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public String readString(String key, @StringRes int defValue) {
        return mSharedPreferences.getString(key, mRes.getString(defValue));
    }

    public void writeString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
        setChanged();
        notifyObservers();
    }

    private static final String COMMON_ROOT_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator
            + "DataMigration";

    private static final String DEF_HELP_FILE_ASSETS_PATH = "help/Def_Help.md";

    public static String getBackupRootDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Backup";
    }

    public static String getReceivedRootDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Received";
    }

    public static String getCompressedRootDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Compressed";
    }

    public static String getCacheRootDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Cache";
    }

    public static String getAppIconCacheRootDir() {
        return getCacheRootDir()
                + File.separator
                + "AppIcons";
    }

    public static String getAppInstallerCacheRootDir() {
        return getCacheRootDir()
                + File.separator
                + "Installer";
    }

    private static String getCommonRootDir() {
        return COMMON_ROOT_DIR;
    }

    public static String getBackupSessionDir(Session session) {
        return getBackupRootDir() + File.separator + session.getName();
    }

    // .DM/Backup/XXXX-XX/session.info
    public static String getBackupSessionInfoPath(Session session) {
        return getBackupSessionDir(session) + File.separator + BACKUP_SESSION_INFO_FILE_NAME;
    }

    // .DM/Backup/XXXX-XX/system.info
    public static String getBackupSystemInfoPath(Session session) {
        return getBackupSessionDir(session) + File.separator + BACKUP_SYSTEM_INFO_FILE_NAME;
    }

    public static String getBackupSessionInfoFileName() {
        return BACKUP_SESSION_INFO_FILE_NAME;
    }

    public static String getRecSessionDir(Session session) {
        return getReceivedRootDir() + File.separator + session.getName();
    }

    public static String getLogDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Logs";
    }

    public static String getTestDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Test";
    }

    public static String getBackupDirByCategory(DataCategory category, Session session) {
        return getBackupRootDir()
                + File.separator
                + session.getName()
                + File.separator
                + category.name();
    }

    public static String getReceivedDirByCategory(DataCategory category, Session session) {
        return getReceivedRootDir()
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

    public static String getEncryptPath(String from) {
        File file = new File(from);
        return file.getParent() + File.separator + file.getName() + "_Encrypt";
    }

    public static String getEncryptedName(String name) {
        return name + "_Encrypt";
    }

    public static boolean isEncryptedName(String name) {
        return name.endsWith("_Encrypt");
    }

    public static String getDecryptPath(String from) {
        return from.substring(0, from.lastIndexOf("_Encrypt"));
    }

    public static boolean isEncryptedFile(String file) {
        return file.endsWith("_Encrypt");
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

    public static boolean isChangeLauncherIconEnabled() {
        return sMe.readBoolean(KEY_CHANGE_LAUNCHER_ICON, R.bool.def_change_launcher_icon_enabled);
    }

    public static void setChangeLauncherIconEnabled(boolean value) {
        sMe.writeBoolean(KEY_CHANGE_LAUNCHER_ICON, value);
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

    public static String getBackupExtraDataDirName() {
        return BACKUP_EXTRA_DATA_DIR_NAME;
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

    public static boolean isAppIntroNoticed() {
        return sMe.readBoolean(KEY_APP_INTRO_NOTICED, false);
    }

    public static boolean isAppIntroNoticed(String version) {
        return isTipsNoticed(KEY_APP_INTRO_NOTICED + "-" + version);
    }

    public static void setAppIntroNoticed(boolean noticed) {
        sMe.writeBoolean(KEY_APP_INTRO_NOTICED, noticed);
    }

    public static void setAppIntroNoticed(String version, boolean noticed) {
        setTipsNoticed(KEY_APP_INTRO_NOTICED + "-" + version, noticed);
    }

    public static String getHelpMdFilePath() {
        return getCommonDataDir()
                + File.separator
                + "Helps";
    }

    public static String getDefHelpFileAssetsPath() {
        return DEF_HELP_FILE_ASSETS_PATH;
    }

    public static String getCommonDataDir() {
        return getDataMigrationRootDir()
                + File.separator
                + "Data";
    }

    public static void setDonateQrPath(String donateQrPath) {
        sMe.writeString(KEY_DONATE_QR_PATH, donateQrPath);
    }

    @Nullable
    public static String getDonateQrPathChecked() {
        String path = sMe.readString(KEY_DONATE_QR_PATH, null);
        if (path == null) return null; // FIX NPE
        if (new File(path).exists()) return path;
        return null;
    }

    public static long getLastUpdateCheckTime() {
        return Long.parseLong(sMe.readString(KEY_LAST_UPDATE_CHECK_TIME, "0"));
    }

    public static void setLastUpdateCheckTime(long time) {
        sMe.writeString(KEY_LAST_UPDATE_CHECK_TIME, String.valueOf(time));
    }

    public static boolean shouldCheckForUpdateNow() {
        return System.currentTimeMillis() - getLastUpdateCheckTime() > Interval.Hour.getIntervalMills();
    }

    public static boolean isTipsNoticed(String tips) {
        String key = KEY_TIPS_NOTICED_PREFIX + tips;
        return sMe.readBoolean(key, false);
    }

    public static void setTipsNoticed(String tips, boolean value) {
        String key = KEY_TIPS_NOTICED_PREFIX + tips;
        sMe.writeBoolean(key, value);
    }

    public static boolean isBugReportEnabled() {
        return sMe.readBoolean(KEY_AUTO_BUG_REPORT, true);
    }

    public static void setBugReportEnabled(boolean value) {
        sMe.writeBoolean(KEY_AUTO_BUG_REPORT, value);
    }

    public static void setAutoInstallAppEnabled(boolean value) {
        sMe.writeBoolean(KEY_AUTO_INSTALL_APP, value);
    }

    public static boolean isAutoInstallAppEnabled() {
        return sMe.readBoolean(KEY_AUTO_INSTALL_APP, true);
    }

    public static void setInstallDataEnabled(boolean value) {
        sMe.writeBoolean(KEY_INSTALL_DATA, value);
    }

    public static boolean isInstallDataEnabled() {
        return sMe.readBoolean(KEY_INSTALL_DATA, false);
    }

    public static ThemeColor getThemeColor() {
        return ThemeColor.valueOf(sMe.readString(KEY_APP_THEME_COLOR, ThemeColor.Default.name()));
    }

    public static void setAppThemeColor(ThemeColor color) {
        sMe.writeString(KEY_APP_THEME_COLOR, color.name());
    }

    public static boolean isEncryptEnabled() {
        return sMe.readBoolean(KEY_ENCRYPT_ENABLED, false);
    }

    public static void setEncryptEnabled(boolean value) {
        sMe.writeBoolean(KEY_ENCRYPT_ENABLED, value);
    }

    public static String getWifiConfigFilePath() {
        return WIFI_CONFIG_FILE_PATH;
    }

    public static String getDataMigrationRootDir() {
        return sMe.readString(KEY_DATA_MIGRATION_ROOT_DIR, COMMON_ROOT_DIR);
    }

    public static void setDataMigrationRootDir(String dir) {
        sMe.writeString(KEY_DATA_MIGRATION_ROOT_DIR, dir);
    }

    public static boolean isShowAdEnabled() {
        return sMe.readBoolean(KEY_SHOW_AD, false);
    }

    public static void setShowAdEnabled(boolean enabled) {
        sMe.writeBoolean(KEY_SHOW_AD, enabled);
    }

    public static InstallerTimeout getAppInstallerTimeout() {
        return InstallerTimeout.valueOf(sMe.readString(KEY_APP_INSTALLER_TIMEOUT, InstallerTimeout.Long.name()));
    }

    public static void setAppInstallerTimeout(InstallerTimeout timeout) {
        sMe.writeString(KEY_APP_INSTALLER_TIMEOUT, String.valueOf(timeout));
    }

    public static int getAdPresentTimes() {
        return Integer.parseInt(sMe.readString(KEY_AD_PRESENT_TIMES, String.valueOf(0)));
    }

    public static void increaseAdPresentTimes() {
        sMe.writeString(KEY_AD_PRESENT_TIMES, String.valueOf(getAdPresentTimes() + 1));
    }

    public static boolean isLoadEnabledForCategory(DataCategory category) {
        switch (category) {
            case SystemSettings:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    return false;
                }
                break;
        }
        return sMe.readBoolean(KEY_LOADER_CONFIG_CATEGORY_ENABLED_PREFIX + category.name(), true);
    }

    public static void setLoadEnabledForCategory(DataCategory category, boolean value) {
        sMe.writeBoolean(KEY_LOADER_CONFIG_CATEGORY_ENABLED_PREFIX + category.name(), value);
    }
}