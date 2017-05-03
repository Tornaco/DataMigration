package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.codechimp.apprater.AppRater;
import org.newstand.datamigration.policy.CoolApkMaket;

public class BaseNavigatorActivity extends TransitionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRater();
    }

    private void initRater() {
        AppRater.app_launched(this);
        AppRater.setMarket(new CoolApkMaket());
    }
}
