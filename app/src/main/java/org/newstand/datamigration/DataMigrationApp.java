package org.newstand.datamigration;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataMigrationApp extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsProvider.init(this);
        Logger.init("DataMigrationApp")
                .methodCount(3)
                .hideThreadInfo()
                .logLevel(LogLevel.FULL);
        Logger.d("DataMigrationApp comes up.");
    }
}
