package org.newstand.datamigration;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.google.common.io.Closer;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.DonateQRPathRetriever;
import org.newstand.datamigration.service.DummSmsServiceProxy;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.utils.OnDeviceLogAdapter;
import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import java.io.Closeable;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataMigrationApp extends Application {

    private static final Closer sCloser = Closer.create();

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static <C extends Closeable> C registerClosable(C closeable) {
        return sCloser.register(closeable);
    }

    public void cleanup() {
        try {
            sCloser.close();
        } catch (IOException e) {
            Logger.e("Fail to close %s", e.getLocalizedMessage());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.config(Settings.builder().tag(getClass().getSimpleName()).logLevel(0).logAdapter(new OnDeviceLogAdapter()).build());
        SettingsProvider.init(this);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        UserActionServiceProxy.startService(this);
        DonateQRPathRetriever.loadAndCache(this);
        DummSmsServiceProxy.startService(this);
    }
}
