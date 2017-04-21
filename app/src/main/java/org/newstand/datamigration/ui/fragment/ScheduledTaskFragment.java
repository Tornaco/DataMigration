package org.newstand.datamigration.ui.fragment;

import com.google.common.collect.ImmutableList;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.service.schedule.BackupActionSettings;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.ScheduleAction;
import org.newstand.datamigration.service.schedule.ScheduleActionType;
import org.newstand.datamigration.ui.tiles.SchedulerActionTile;
import org.newstand.datamigration.worker.transport.Session;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledTaskFragment extends DashboardFragment {
    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category prebuilt = new Category();

        ScheduleAction template = ScheduleAction.builder()
                .actionType(ScheduleActionType.Backup)
                .settings(BackupActionSettings.builder()
                        .session(Session.from("SchedulerService"))
                        .dataCategories(ImmutableList.of(
                                DataCategory.CallLog,
                                DataCategory.Contact))
                        .build())
                .build();

        Condition condition = Condition.builder()
                .isPersisted(true)
                .requiresCharging(true)
                .requiresDeviceIdle(true)
                .build();

        SchedulerActionTile schedulerActionTile = new SchedulerActionTile(getContext(), condition, template);

        prebuilt.addTile(schedulerActionTile);
    }
}
