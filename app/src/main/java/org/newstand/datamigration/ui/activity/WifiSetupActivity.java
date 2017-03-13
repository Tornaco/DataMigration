package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.net.WifiAssistant;
import org.newstand.datamigration.thread.SharedExecutor;

/**
 * Created by Nick@NewStand.org on 2017/3/13 15:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WifiSetupActivity extends TransactionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getTitle());

        final WifiAssistant wifiAssistant = new WifiAssistant(this);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
               wifiAssistant.waitForAP("Lenovo");
            }
        });
    }
}
