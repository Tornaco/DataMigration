package org.newstand.datamigration.utils;

import com.chrisplus.rootmanager.RootManager;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;

/**
 * Created by guohao4 on 2017/7/11.
 */

public class RootChecker {

    public static void checkRootAndApplySettingsAsync() {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!RootManager.getInstance().hasRooted()) {
                    SettingsProvider.setInstallDataEnabled(false);
                    SettingsProvider.setAutoInstallAppEnabled(false);
                }
            }
        });
    }
}
