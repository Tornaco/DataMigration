package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.ui.fragment.ScheduledTaskCreatorFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:40
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledTaskCreatorActivity extends TransitionSafeActivity implements Producer<Integer> {

    private int queryId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
        Intent intent = getIntent();
        queryId = intent.getIntExtra(IntentEvents.KEY_ACTION_SCHEDULE_TASK, queryId);
        replaceV4(R.id.container, new ScheduledTaskCreatorFragment(), null);
    }

    @Override
    public Integer produce() {
        return queryId;
    }
}
