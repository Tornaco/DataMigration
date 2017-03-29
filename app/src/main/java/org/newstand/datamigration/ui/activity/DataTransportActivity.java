package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataTransportActivity extends TransitionSafeActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
    }

    @Override
    protected void onSmoothHook() {
        super.onSmoothHook();
        replaceV4(R.id.container, getTransportFragment(), null);
    }

    protected Fragment getTransportFragment() {
        return null;
    }

    @Override
    protected boolean needSmoothHook() {
        return true;
    }

    @Override
    public void finish() {
        finishWithAfterTransition();
    }
}
