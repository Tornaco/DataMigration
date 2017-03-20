package org.newstand.datamigration.utils;

import android.content.Context;
import android.os.PowerManager;

import java.util.concurrent.ConcurrentHashMap;

public class WakeLockWrapper {

    private static final ConcurrentHashMap<String, PowerManager.WakeLock> sWakeLockMap = new ConcurrentHashMap<String, PowerManager.WakeLock>();

    public static PowerManager.WakeLock getWakeLockInstance(Context context, String tag) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (!sWakeLockMap.containsKey(tag)) {
            sWakeLockMap.put(tag, powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag));
        }
        return sWakeLockMap.get(tag);
    }
}
