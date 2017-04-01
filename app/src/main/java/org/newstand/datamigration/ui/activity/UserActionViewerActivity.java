package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.UserActionListAdapter;
import org.newstand.logger.Logger;

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

    @Getter
    private long fingerPrint = -1L;

    private
    @StyleRes
    int theme = R.style.AppTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        resolveIntent();
        setTheme(theme);

        super.onCreate(savedInstanceState);

        showHomeAsUp();

        setContentView(R.layout.recycler_view_template);

        setupView();
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            fingerPrint = intent.getLongExtra(IntentEvents.KEY_USERACTION_FINGER_PRINT, fingerPrint);
            theme = R.style.UserActionViewerTheme;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadByFinger();
    }

    private void setupView() {

        View root = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // No divider in Dialog theme?
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadByFinger();
            }
        });
    }

    UserActionListAdapter onCreateAdapter() {
        return new UserActionListAdapter(this);
    }

    private void loadByFinger() {

        Logger.d("Loading us by finger %d", getFingerPrint());

        getSwipeRefreshLayout().setRefreshing(true);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<UserAction> all =
                        getFingerPrint() > 0 ?
                                UserActionServiceProxy.getByFingerPrint(getApplicationContext(), getFingerPrint())
                                : UserActionServiceProxy.getAll(getApplicationContext());
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
