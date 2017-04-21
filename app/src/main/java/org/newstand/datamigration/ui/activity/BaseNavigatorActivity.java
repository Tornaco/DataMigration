package org.newstand.datamigration.ui.activity;

import android.os.Bundle;

public class BaseNavigatorActivity extends TransitionSafeActivity {

    protected void setupView() {
    }

    @Override
    public boolean isMainActivity() {
        return true;
    }
}
