package org.newstand.datamigration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/7 13:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DummySmsService extends Service {

    final Handler handler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Logger.d("DummySmsService created~");
        super.onCreate();
        checkDefSmsAppInLoop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void checkDefSmsAppInLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SmsContentProviderCompat.areWeDefSmsApp(getApplicationContext())) {
                    stopSelf();
                    return;
                }
                SmsContentProviderCompat.restoreDefSmsAppRetentionCheckedAsync(getApplicationContext());
                handler.postDelayed(this, Interval.Minutes.getIntervalMills());
            }
        }, Interval.Minutes.getIntervalMills());
    }

    @Override
    public void onDestroy() {
        Logger.d("DummySmsService destroy~");
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
