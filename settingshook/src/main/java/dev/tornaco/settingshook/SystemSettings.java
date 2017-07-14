package dev.tornaco.settingshook;

import android.Manifest;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

/**
 * Created by Nick on 2017/6/21 13:23
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public enum SystemSettings {

    Global(new SettingsItem[]{
            new SettingsItem(Settings.Global.CAPTIVE_PORTAL_DETECTION_ENABLED, "CAPTIVE_PORTAL_DETECTION_ENABLED"),
            new SettingsItem(Settings.Global.CAPTIVE_PORTAL_SERVER, "CAPTIVE_PORTAL_SERVER"),
            new SettingsItem(Settings.Global.CAPTIVE_PORTAL_USE_HTTPS, "CAPTIVE_PORTAL_USE_HTTPS"),
            new SettingsItem(Settings.Global.AUTO_TIME, "AUTO_TIME"),
            new SettingsItem(Settings.Global.AUTO_TIME_ZONE, "AUTO_TIME_ZONE"),
            new SettingsItem(Settings.Global.POWER_SOUNDS_ENABLED, "POWER_SOUNDS_ENABLED"),
            new SettingsItem(Settings.Global.CHARGING_SOUNDS_ENABLED, "CHARGING_SOUNDS_ENABLED"),
            new SettingsItem(Settings.Global.STAY_ON_WHILE_PLUGGED_IN, "STAY_ON_WHILE_PLUGGED_IN"),
            new SettingsItem(Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL, "LOW_POWER_MODE_TRIGGER_LEVEL"),
            new SettingsItem(Settings.Global.HEADS_UP_NOTIFICATIONS_ENABLED, "HEADS_UP_NOTIFICATIONS_ENABLED"),
    }),

    System(new SettingsItem[]{
            new SettingsItem(Settings.System.VIBRATE_ON, "VIBRATE_ON"),
            new SettingsItem(Settings.System.VOLUME_ALARM, "VOLUME_ALARM"),
            new SettingsItem(Settings.System.VOLUME_RING, "VOLUME_RING"),
            new SettingsItem(Settings.System.VOLUME_MUSIC, "VOLUME_MUSIC"),
            new SettingsItem(Settings.System.VOLUME_NOTIFICATION, "VOLUME_NOTIFICATION"),
            new SettingsItem(Settings.System.SHOW_TOUCHES, "SHOW_TOUCHES"),
            new SettingsItem(Settings.System.VOLUME_SYSTEM, "VOLUME_SYSTEM"),
    });

    public SettingsItem[] definations;

    SystemSettings(SettingsItem[] definations) {
        this.definations = definations;
    }
}
