package org.newstand.datamigration.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;

/**
 * Created by Nick@NewStand.org on 2017/4/26 10:16
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WifiUtils {

    private static final int MAX_TRY_TIMES = 20;

    public static boolean setWifiEnabled(Context context, boolean value) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(value);

        boolean current = wifiManager.isWifiEnabled();
        int time = 0;
        while (current != value) {

            if (time > MAX_TRY_TIMES) {
                return false;
            }

            Sleeper.sleepQuietly(Interval.Seconds.getIntervalMills());
            time++;
            current = wifiManager.isWifiEnabled();
        }

        return true;
    }
}
