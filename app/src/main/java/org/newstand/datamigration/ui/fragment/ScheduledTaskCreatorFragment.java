package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.service.schedule.ActionSettings;
import org.newstand.datamigration.service.schedule.BackupActionSettings;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.ui.tiles.ConditionChargeTile;
import org.newstand.datamigration.ui.tiles.ConditionIdleTile;
import org.newstand.datamigration.ui.tiles.ConditionNetworkTypeTile;
import org.newstand.datamigration.ui.tiles.ConditionPersistTile;
import org.newstand.datamigration.ui.tiles.ScheduledBackupActionCategoriesSettingsTile;
import org.newstand.datamigration.ui.tiles.ScheduledBackupActionSessionSettingsTile;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledTaskCreatorFragment extends DashboardFragment {

    ActionSettings actionSettings = null;

    Condition condition = Condition.builder()
            .isPersisted(true)
            .requiresCharging(true)
            .requiresDeviceIdle(true)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        FloatingActionButton fab = null;
        if (root != null) {
            fab = (FloatingActionButton) root.findViewById(R.id.fab);
        }
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClick();
                }
            });
        }
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDefSettings();
    }

    private void createDefSettings() {
        actionSettings = BackupActionSettings.builder()
                .session(Session.from(getString(R.string.title_settings_session_def)))
                .dataCategories(new ArrayList<DataCategory>(DataCategory.values().length))
                .build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_scheduled_task;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category conditions = new Category();
        conditions.titleRes = R.string.title_conditions;

        ConditionIdleTile conditionIdleTile = new ConditionIdleTile(getContext(), condition);
        ConditionChargeTile conditionChargeTile = new ConditionChargeTile(getContext(), condition);
        ConditionPersistTile conditionPersistTile = new ConditionPersistTile(getContext(), condition);
        ConditionNetworkTypeTile conditionNetworkTypeTile = new ConditionNetworkTypeTile(getContext(), condition);

        conditions.addTile(conditionChargeTile);
        conditions.addTile(conditionIdleTile);
        conditions.addTile(conditionPersistTile);
        conditions.addTile(conditionNetworkTypeTile);

        Category settings = new Category();
        settings.titleRes = R.string.action_settings;

        settings.addTile(new ScheduledBackupActionSessionSettingsTile(getContext(), actionSettings));
        settings.addTile(new ScheduledBackupActionCategoriesSettingsTile(getContext(), actionSettings));

        categories.add(conditions);
        categories.add(settings);
    }

    private void onFabClick() {
        Logger.d("OnClick, condition %s settings %s", condition, actionSettings);
    }
}
