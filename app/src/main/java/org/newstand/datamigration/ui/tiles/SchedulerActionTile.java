package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.ScheduleAction;
import org.newstand.datamigration.ui.activity.ScheduledTaskCreatorActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SchedulerActionTile extends ThemedTile {

    private int id = -1;

    public SchedulerActionTile(@NonNull Context context,
                               Condition condition,
                               ScheduleAction scheduleAction) {
        super(context, null);
        this.title = scheduleAction.getSettings().getSession().getName();
        this.iconRes = scheduleAction.getActionType().iconRes();
        this.id = (int) scheduleAction.getId();
    }

    @Override
    void onInitView(final Context context) {

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) context;
                Intent intent = new Intent(getContext(), ScheduledTaskCreatorActivity.class);
                intent.putExtra(IntentEvents.KEY_ACTION_SCHEDULE_TASK, id);
                transitionSafeActivity.transitionTo(intent);
            }
        };
    }
}
