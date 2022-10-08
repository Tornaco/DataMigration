package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.License;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.impl.LicenseLoader;
import org.newstand.datamigration.ui.adapter.LicenseListAdapter;

import java.util.Collection;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LicenseViewerActivity extends TransitionSafeActivity {

    @Getter
    RecyclerView recyclerView;

    @Getter
    private LicenseListAdapter adapter;

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
        startLoading();
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
                startLoading();
            }
        });
    }

    LicenseListAdapter onCreateAdapter() {
        return new LicenseListAdapter(this);
    }

    private void startLoading() {
        LicenseLoader.loadAsync(this, new LoaderListenerMainThreadAdapter<License>() {
            @Override
            public void onCompleteMainThread(Collection<License> collection) {
                super.onCompleteMainThread(collection);
                getAdapter().update(collection);
                getSwipeRefreshLayout().setRefreshing(false);
            }
        });
    }
}
