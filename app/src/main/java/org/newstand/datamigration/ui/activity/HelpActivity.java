package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:44
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class HelpActivity extends TransitionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setContentView(R.layout.help);
    }
}
