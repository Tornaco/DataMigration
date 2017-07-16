package org.newstand.datamigration.data;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Nick@NewStand.org on 2017/3/13 13:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SmsContentProviderCompat {

    public static final Uri INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");
    public static final Uri SENT_CONTENT_URI = Uri.parse("content://sms/sent");
    public static final Uri DRAFT_CONTENT_URI = Uri.parse("content://sms/draft");

    public static final String ADDRESS = "address";
    public static final String BODY = "body";
    public static final String DATE_SENT = "date_sent";
    public static final String READ = "read";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setAsDefaultSmsApp(final Context context) {

        String defaultSmsApp = null;
        final String us = context.getPackageName();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
            Logger.d("setAsDefaultSmsApp, current is:%s", defaultSmsApp);
        }

        if (TextUtils.isEmpty(defaultSmsApp)) {
            Logger.e("Fail get default SMS app");
            return;
        }

        if (!defaultSmsApp.equals(us)) {
            SettingsProvider.setDefSmsApp(defaultSmsApp);
            SharedExecutor.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    new MaterialStyledDialog.Builder(context)
                            .setTitle(R.string.warn_def_sms_app_title)
                            .setDescription(R.string.warn_need_def_sms_app_message)
                            .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                            .withDarkerOverlay(false)
                            .setCancelable(false)
                            .setPositiveText(android.R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {
                                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, us);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }).show();
                }
            });
        }
    }

    public static boolean waitUtilBecomeDefSmsApp(Context context) {
        return waitUtilBecomeDefSmsApp(context, 10);
    }

    public static boolean waitUtilBecomeDefSmsApp(Context context, int maxTimes) {

        for (int i = 0; i < maxTimes; i++) {
            String defaultSmsApp;
            String currentPn = context.getPackageName();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
            } else {
                return true;
            }

            if (currentPn.equals(defaultSmsApp)) {
                return true;
            }

            Sleeper.sleepQuietly(1000);
        }

        return false;
    }

    public static boolean areWeDefSmsApp(Context context) {
        String defaultSmsApp;

        String me = context.getPackageName();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
        } else {
            return false;
        }

        return !TextUtils.isEmpty(defaultSmsApp) && me.equals(defaultSmsApp);

    }

    public static void restoreDefSmsAppRetentionCheckedBlocked(final Context context) {
        if (!areWeDefSmsApp(context)) {
            Logger.v("restoreDefSmsAppRetentionCheckedAsync, we are not, no need");
            return;
        }
        Logger.d("restoreDefSmsAppRetentionCheckedAsync");
        final CountDownLatch latch = new CountDownLatch(1);
        SharedExecutor.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                new MaterialStyledDialog.Builder(context)
                        .setTitle(R.string.warn_def_sms_app_title)
                        .setDescription(R.string.warn_restore_def_sms_app_message)
                        .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                        .withDarkerOverlay(false)
                        .setCancelable(false)
                        .setPositiveText(android.R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                SharedExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        latch.countDown();
                                        restoreDefSmsApp(context);
                                    }
                                });
                            }
                        }).show();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException ignored) {

        }
    }

    public static void restoreDefSmsAppRetentionCheckedAsync(final Context context) {
        if (!areWeDefSmsApp(context)) {
            Logger.v("restoreDefSmsAppRetentionCheckedAsync, we are not, no need");
            return;
        }
        Logger.d("restoreDefSmsAppRetentionCheckedAsync");
        new MaterialStyledDialog.Builder(context)
                .setTitle(R.string.warn_def_sms_app_title)
                .setDescription(R.string.warn_restore_def_sms_app_message)
                .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                .withDarkerOverlay(false)
                .setCancelable(false)
                .setPositiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        SharedExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                restoreDefSmsApp(context);
                            }
                        });
                    }
                }).show();
    }

    @WorkerThread
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void restoreDefSmsApp(Context context) {
        String defaultSmsApp = null;

        String me = context.getPackageName();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
        }

        if (TextUtils.isEmpty(defaultSmsApp)) {
            Logger.e("Fail get default SMS app");
            return;
        }

        // Find the most like app.
        if (me.equals(defaultSmsApp)) {
            Logger.w("Current def SMS app is us, restoring...");
            String previousPkg = SettingsProvider.getDefSmsApp();
            if (TextUtils.isEmpty(previousPkg)) {
                Logger.e("No def SMS app stored in Settings");
                return;
            }
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, previousPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static boolean isAndroidMmsApp(Context context, String strPkgName) {

        if (context.getPackageName().equals(strPkgName)) return false;
        if (!strPkgName.contains("android") && !strPkgName.contains("google")) return false;

        PackageManager pkm = context.getPackageManager();
        boolean isMms = false;
        PackageInfo pkgInfo;
        try {
            pkgInfo = pkm.getPackageInfo(strPkgName, PackageManager.GET_SERVICES);
            ServiceInfo[] servicesInfos = pkgInfo.services;
            if (null != servicesInfos) {
                for (ServiceInfo sInfo : servicesInfos) {
                    if (null != sInfo.permission && sInfo.permission.equals(Manifest.permission.SEND_SMS)) {
                        isMms = true;
                        break;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return isMms;
    }

}
