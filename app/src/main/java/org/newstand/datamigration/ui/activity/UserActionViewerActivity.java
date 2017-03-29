package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.UserActionListAdapter;

import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionViewerActivity extends TransitionSafeActivity {

    @Getter
    RecyclerView recyclerView;

    @Getter
    private UserActionListAdapter adapter;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showHomeAsUp();

        setContentView(R.layout.recycler_view_template);

        setupView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        load();
    }

    private void setupView() {

        View root = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
    }

    UserActionListAdapter onCreateAdapter() {
        return new UserActionListAdapter(this);
    }

    private void load() {
        getSwipeRefreshLayout().setRefreshing(true);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<UserAction> all = UserActionServiceProxy.getAll(getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAdapter().update(all);
                        getSwipeRefreshLayout().setRefreshing(false);
                    }
                });
            }
        });
    }
}
