package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.ui.fragment.RulesCreatorFragment;
import org.newstand.datamigration.ui.fragment.ScheduledTaskCreatorFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:40
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RulesCreatorActivity extends TransitionSafeActivity implements Producer<String> {

    private String pkgName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
        Intent intent = getIntent();
        pkgName = intent.getStringExtra(IntentEvents.KEY_PKG_NAME);
        replaceV4(R.id.container, new RulesCreatorFragment(), null);
    }

    @Override
    public String produce() {
        return pkgName;
    }
}
