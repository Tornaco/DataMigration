package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.fragment.ExtraRulesViewerFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:40
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ExtraRulesViewerActivity extends TransitionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
        replaceV4(R.id.container, new ExtraRulesViewerFragment(), null);
    }
}
