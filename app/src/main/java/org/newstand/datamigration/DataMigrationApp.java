package org.newstand.datamigration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.bugsnag.android.Bugsnag;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.DonateQRPathRetriever;
import org.newstand.datamigration.service.DummSmsServiceProxy;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.utils.OnDeviceLogAdapter;
import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataMigrationApp extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public Activity getTopActivity() {
        return topActivityObserver.getTopActivity();
    }

    @Getter
    private TopActivityObserver topActivityObserver = new TopActivityObserver();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Bugsnag.init(this);

        SettingsProvider.init(this);

        Logger.config(Settings.builder()
                .tag(getClass().getSimpleName())
                .logLevel(SettingsProvider.isDebugEnabled() ? Logger.LogLevel.ALL : Logger.LogLevel.WARN)
                .logAdapter(new OnDeviceLogAdapter())
                .bugReportEnabled(SettingsProvider.isBugReportEnabled())
                .build());

        DonateQRPathRetriever.loadAndCache(this);

        // Setup observer
        topActivityObserver.setOnMainActivityDestroyConsumer(new Consumer<Activity>() {
            @Override
            public void accept(@NonNull Activity activity) {
                Logger.d("MainActivity has been finished, cleaning...");
                cleanup();
            }
        });
        topActivityObserver.setOnMainActivityStartConsumer(new Consumer<Activity>() {
            @Override
            public void accept(@NonNull Activity activity) {
                Logger.d("MainActivity has been started, starting core service...");
                startCore();
            }
        });
        registerActivityLifecycleCallbacks(topActivityObserver);
    }

    private void startCore() {
        DummSmsServiceProxy.startService(this);
        UserActionServiceProxy.startService(getApplicationContext());
    }

    private void cleanup() {
        UserActionServiceProxy.stopService(getApplicationContext());
    }
}
