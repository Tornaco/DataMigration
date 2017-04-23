package org.newstand.datamigration.service.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.concurrent.CountDownLatch;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/23 21:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BatteryStatusRetriever {

    @Getter
    private boolean isCharging = false;

    private CountDownLatch latch = new CountDownLatch(1);

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    isCharging = true;
                    latch.countDown();
                }
            }
        }
    };

    public void register(Context context) {
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void waitForResult() {
        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException ignored) {

            }
        }
    }

    public static boolean isCharging(Context context) {
        BatteryStatusRetriever receiver = new BatteryStatusRetriever();
        receiver.register(context);
        receiver.waitForResult();
        return receiver.isCharging();
    }
}