package org.newstand.datamigration.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.repo.SchedulerParamRepoService;
import org.newstand.datamigration.service.schedule.SchedulerParam;
import org.newstand.datamigration.ui.activity.ScheduledTaskCreatorActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.tiles.SchedulerActionTile;
import org.newstand.datamigration.utils.Collections;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledTaskViewerFragment extends DashboardFragment {

    private List<SchedulerParam> mSchedulerParamList;

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

    private void onFabClick() {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(new Intent(getContext(), ScheduledTaskCreatorActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_scheduled_task_viewer;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        if (Collections.isNullOrEmpty(mSchedulerParamList)) return;

        final Category task = new Category();

        Collections.consumeRemaining(mSchedulerParamList, new Consumer<SchedulerParam>() {
            @Override
            public void accept(@NonNull SchedulerParam schedulerParam) {
                SchedulerActionTile tile = new SchedulerActionTile(getActivity(),
                        schedulerParam.getCondition(), schedulerParam.getAction());
                task.addTile(tile);
            }
        });

        categories.add(task);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLoading();
    }

    private void startLoading() {
        mSchedulerParamList = SchedulerParamRepoService.get().findAll(getContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buildUI(getContext());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSchedulerParamList != null)
            mSchedulerParamList.clear();
        mSchedulerParamList = null;
    }
}
