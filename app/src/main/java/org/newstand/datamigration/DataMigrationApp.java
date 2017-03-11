package org.newstand.datamigration;

import android.app.Application;
import android.graphics.Typeface;

import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataMigrationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize typeface helper
        TypefaceCollection typeface = new TypefaceCollection.Builder()
                .set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), "fonts/xy.ttf"))
                .create();
        TypefaceHelper.init(typeface);
        Logger.init("DataMigrationApp")
                .methodCount(3)
                .hideThreadInfo()
                .logLevel(LogLevel.FULL);
        Logger.d("DataMigrationApp comes up.");
    }
}
