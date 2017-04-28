package org.newstand.datamigration.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.policy.ExtraDataRule;
import org.newstand.datamigration.repo.ExtraDataRulesRepoService;
import org.newstand.datamigration.ui.activity.RulesCreatorActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.tiles.RulesIntroTile;
import org.newstand.datamigration.ui.tiles.RulesViewerTile;
import org.newstand.datamigration.utils.Collections;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ExtraRulesViewerFragment extends DashboardFragment {

    private List<ExtraDataRule> mRulesList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        FloatingActionButton fab = null;
        if (root != null) {
            fab = (FloatingActionButton) root.findViewById(R.id.fab);
            swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        }
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClick();
                }
            });
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    startLoading();
                }
            });
        }
        return root;
    }

    private void onFabClick() {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
        transitionSafeActivity.transitionTo(new Intent(getContext(), RulesCreatorActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_extra_rules_viewer;
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category intro = new Category();
        intro.titleRes = R.string.category_rule_intro;

        intro.addTile(new RulesIntroTile(getContext()));

        categories.add(intro);

        if (Collections.isNullOrEmpty(mRulesList)) return;

        final Category my = new Category();
        my.titleRes = R.string.title_my_rules;

        Collections.consumeRemaining(mRulesList,
                new Consumer<ExtraDataRule>() {
                    @Override
                    public void accept(@NonNull ExtraDataRule dataRule) {
                        RulesViewerTile rulesViewerTile = new RulesViewerTile(getActivity(), dataRule);
                        my.addTile(rulesViewerTile);
                    }
                });

        categories.add(my);
    }

    @Override
    public void onResume() {
        super.onResume();
        startLoading();
    }

    private void startLoading() {
        swipeRefreshLayout.setRefreshing(true);
        mRulesList = ExtraDataRulesRepoService.get().findAll(getContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buildUI(getContext());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRulesList != null)
            mRulesList.clear();
        mRulesList = null;
    }
}
