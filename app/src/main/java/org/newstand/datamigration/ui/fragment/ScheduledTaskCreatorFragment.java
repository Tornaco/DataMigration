package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.repo.SchedulerParamRepoService;
import org.newstand.datamigration.service.schedule.BackupActionSettings;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.ScheduleAction;
import org.newstand.datamigration.service.schedule.ScheduleActionType;
import org.newstand.datamigration.service.schedule.SchedulerParam;
import org.newstand.datamigration.service.schedule.SchedulerServiceProxy;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.tiles.ConditionChargeTile;
import org.newstand.datamigration.ui.tiles.ConditionIdleTile;
import org.newstand.datamigration.ui.tiles.ConditionNetworkTypeTile;
import org.newstand.datamigration.ui.tiles.ConditionPersistTile;
import org.newstand.datamigration.ui.tiles.ConditionRepeatTile;
import org.newstand.datamigration.ui.tiles.ConditionTimeTile;
import org.newstand.datamigration.ui.tiles.ScheduledBackupActionCategoriesSettingsTile;
import org.newstand.datamigration.ui.tiles.ScheduledBackupActionSessionSettingsTile;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.DateUtils;
import org.newstand.datamigration.worker.transport.Session;

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

    private SchedulerParam param;
    private Producer<Integer> idProducer;

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
                    onFabClick(v);
                }
            });
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        idProducer = (Producer<Integer>) context;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.task_viewer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_remove) {
            if (param == null) {
                return false;
            }
            SchedulerParam ex = SchedulerParamRepoService.get()
                    .findById(getContext(), idProducer.produce());

            if (ex == null) {
                return false;
            }

            SchedulerParamRepoService.get().delete(getContext(), ex);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadOrCreateParams();
    }

    private void loadOrCreateParams() {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int id = idProducer.produce();
                if (id < 0) {
                    BackupActionSettings actionSettings = BackupActionSettings.builder()
                            .session(Session.from(getString(R.string.title_settings_session_def)
                                    + "@"
                                    + DateUtils.formatForFileName(System.currentTimeMillis())))
                            .dataCategories(new ArrayList<DataCategory>(DataCategory.values().length))
                            .build();
                    param = new SchedulerParam(Condition.DEFAULT,
                            ScheduleAction.builder()
                                    .actionType(ScheduleActionType.Backup)
                                    .settings(actionSettings).build());
                } else {
                    param = SchedulerParamRepoService.get().findById(getContext(), id);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buildUI(getContext());
                    }
                });
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_scheduled_task;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        if (param == null) {
            return;
        }

        Category conditions = new Category();
        conditions.titleRes = R.string.title_conditions;

        Condition condition = param.getCondition();

        ConditionIdleTile conditionIdleTile = new ConditionIdleTile(getContext(), condition);
        ConditionChargeTile conditionChargeTile = new ConditionChargeTile(getContext(), condition);
        ConditionPersistTile conditionPersistTile = new ConditionPersistTile(getContext(), condition);
        ConditionNetworkTypeTile conditionNetworkTypeTile = new ConditionNetworkTypeTile(getContext(), condition);
        ConditionTimeTile conditionTimeTile = new ConditionTimeTile(getContext(), condition);
        ConditionRepeatTile conditionRepeatTile = new ConditionRepeatTile(getContext(), condition);

        conditions.addTile(conditionChargeTile);
        conditions.addTile(conditionIdleTile);
        conditions.addTile(conditionPersistTile);
        conditions.addTile(conditionNetworkTypeTile);
        conditions.addTile(conditionTimeTile);
        conditions.addTile(conditionRepeatTile);

        Category settings = new Category();
        settings.titleRes = R.string.action_settings;

        settings.addTile(new ScheduledBackupActionSessionSettingsTile(getContext(), param.getAction().getSettings()));
        settings.addTile(new ScheduledBackupActionCategoriesSettingsTile(getContext(), param.getAction().getSettings()));

        categories.add(conditions);
        categories.add(settings);
    }

    private void onFabClick(View view) {

        if (!validateParam(view)) return;

        SchedulerServiceProxy.schedule(getContext(), param.getCondition(), param.getAction());
        getActivity().finish();
    }

    private boolean validateParam(View view) {

        if (Collections.isNullOrEmpty(param.getAction().getSettings().getDataCategories())) {

            Snackbar.make(view, R.string.title_validation_select_data, Snackbar.LENGTH_LONG).show();

            return false;
        }

        return true;
    }
}
