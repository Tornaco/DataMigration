package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.ScheduleAction;
import org.newstand.logger.Logger;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SchedulerActionTile extends ThemedTile {

    public SchedulerActionTile(@NonNull Context context,
                               Condition condition,
                               ScheduleAction scheduleAction) {
        super(context, null);
        this.title = context.getString(scheduleAction.getActionType().nameRes());
        this.iconRes = scheduleAction.getActionType().iconRes();

        Logger.d("Creating SchedulerActionTile %s", scheduleAction);
    }

    @Override
    void onInitView(Context context) {

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
            }
        };
    }
}
