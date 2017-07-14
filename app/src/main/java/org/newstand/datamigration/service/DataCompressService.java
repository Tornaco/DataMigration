package org.newstand.datamigration.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener;
import org.newstand.datamigration.utils.MediaScannerClient;
import org.newstand.datamigration.utils.SevenZipper;
import org.newstand.datamigration.utils.ZipUtils;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nick on 2017/6/27 21:49
 */

public class DataCompressService extends Service implements DataCompresser {

    private static final int NOTIFICATION_ID = 6888;
    private static final AtomicInteger sNotificationId = new AtomicInteger(NOTIFICATION_ID);

    private Handler mHandler;

    private final Set<Integer> sTasks = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread hr = new HandlerThread(getClass().getSimpleName());
        hr.start();
        mHandler = new Handler(hr.getLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceStub();
    }

    @Override
    public void compressAsync(final String src, final String dest,
                              final ActionListener<Boolean> listener) {

        // Show notification.
        final int id = setupCompressingNotification(getApplicationContext());

        sTasks.add(id);

        Logger.d("Add compress task:%s", id);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean res = false;//SevenZipper.compressTar(src, dest);
                File destFile = new File(dest);
                try {
                    ZipUtils.zip(src, destFile.getParent(), destFile.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (destFile.exists() && destFile.length() > 0) {
                    res = true;
                }
                Logger.d("Compress complete");

                if (res) {
                    try {
                        MediaScannerClient.scanSync(getApplicationContext(), dest);
                        MediaScannerClient.scanSync(getApplicationContext(), new File(dest).getParent());
                    } catch (InterruptedException ignored) {

                    }
                }

                clearCompressingNotification(getApplicationContext(), id);
                setupCompressedNotification(dest, res, getApplicationContext());

                sTasks.remove(id);

                try {
                    listener.onAction(res);
                } catch (Throwable e) {
                    Logger.w("Error call listener:" + e.getLocalizedMessage());
                }

                if (sTasks.isEmpty()) {
                    stopSelf();
                }
            }
        });
    }

    private synchronized int setupCompressingNotification(Context context) {
        Logger.d("Setting up the notification");
        // Get NotificationManager reference
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Instantiate a Notification
        int icon = R.drawable.ic_compressing;
        long when = System.currentTimeMillis();

        // Define Notification's message and Intent
        CharSequence contentTitle = context.getString(R.string.action_compress);
        CharSequence contentText = getString(R.string.action_compressing);

        Notification.Builder nb = new Notification.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                // .setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setTicker(getString(R.string.action_compressing))
                .setWhen(when)
                .setOngoing(true);

        Notification notification;

        // go delegate high to low android version adding extra options
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nb.setVisibility(Notification.VISIBILITY_PUBLIC);
            nb.setCategory(Notification.CATEGORY_SERVICE);
            nb.setPriority(Notification.PRIORITY_MAX);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            nb.setShowWhen(false);
            notification = nb.build();
        } else {
            notification = nb.getNotification();
        }

        // Pass Notification to NotificationManager
        int id = sNotificationId.incrementAndGet();
        nm.notify(id, notification);

        Logger.d("Notification setup done");

        return id;
    }

    private void clearCompressingNotification(Context context, int id) {
        Logger.d("Clearing the notifications");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
        Logger.d("Cleared notification");
    }

    private void setupCompressedNotification(String dest, boolean res, Context context) {
        Logger.d("Setting up the notification");
        // Get NotificationManager reference
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Instantiate a Notification
        int icon = R.drawable.ic_compressing;
        long when = System.currentTimeMillis();

        // Define Notification's message and Intent
        CharSequence contentTitle = context.getString(R.string.action_compressed);

        CharSequence contentText = res ? getString(R.string.action_compressed_to, dest)
                : getString(R.string.action_compress_fail);

        Notification.Builder nb = new Notification.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(icon)
                .setTicker(getString(R.string.action_compressing))
                .setWhen(when)
                .setOngoing(false);

        Notification notification;

        // go delegate high to low android version adding extra options
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nb.setVisibility(Notification.VISIBILITY_PUBLIC);
            nb.setCategory(Notification.CATEGORY_SERVICE);
            nb.setPriority(Notification.PRIORITY_MAX);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            nb.setShowWhen(false);
            notification = nb.build();
        } else {
            notification = nb.getNotification();
        }

        // Pass Notification to NotificationManager
        nm.notify(sNotificationId.incrementAndGet(), notification);

        Logger.d("Notification setup done");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class ServiceStub extends Binder implements DataCompresser {
        @Override
        public void compressAsync(String src, String dest, ActionListener<Boolean> listener) {
            DataCompressService.this.compressAsync(src, dest, listener);
        }
    }
}
