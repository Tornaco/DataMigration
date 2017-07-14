package org.newstand.datamigration.provider;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.ui.activity.NavigatorActivity;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.Observable;
import java.util.Observer;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/19 12:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ThemeManager implements Observer {

    @Getter
    private ThemeColor themeColor;

    private Context context;

    public ThemeManager(Context context) {
        this.context = context;
        this.themeColor = SettingsProvider.getThemeColor();
    }

    public void disable(ThemeColor themeColor) {

        String currentActivity = NavigatorActivity.class.getName() + themeColor.name();

        Logger.d("Disabling %s", currentActivity);

        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), currentActivity);
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void enable(ThemeColor themeColor) {
        String currentActivity = NavigatorActivity.class.getName() + themeColor.name();

        Logger.d("Enabling %s", currentActivity);

        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), currentActivity);
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void init(Context context) {
        register(context);
    }

    private static void register(Context context) {
        SettingsProvider.observe(new ThemeManager(context));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!SettingsProvider.isChangeLauncherIconEnabled()) return;
        ThemeColor color = SettingsProvider.getThemeColor();
        if (color != themeColor) {
            themeColor = color;
            onColorChange();
        }
    }

    private void onColorChange() {
        Collections.consumeRemaining(ThemeColor.values(), new Consumer<ThemeColor>() {
            @Override
            public void accept(@NonNull ThemeColor color) {
                if (color != themeColor) disable(color);
            }
        });
        enable(themeColor);
    }
}
