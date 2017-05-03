package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;

import java.util.ArrayList;
import java.util.Collection;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 14:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class DataListViewerFragment extends TransitionSafeFragment {

    @Getter
    RecyclerView recyclerView;

    @Getter
    FloatingActionButton fab;

    @Getter
    private CommonListAdapter adapter;

    @Getter
    private LoaderSourceProvider loaderSourceProvider;

    @Getter
    private ListStateListener listStateListener;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loaderSourceProvider = (LoaderSourceProvider) getActivity();
        listStateListener = new ListStateListener() {
            @Override
            public void onScroll() {
                fab.hide();
            }

            @Override
            public void onIdle() {
                fab.show();
            }
        };

        setHasOptionsMenu(true);
    }

    private LoadingCacheManager getCache(LoaderSource source) {
        switch (source.getParent()) {
            case Android:
                return LoadingCacheManager.droid();
            case Backup:
                return LoadingCacheManager.bk();
            case Received:
                return LoadingCacheManager.received();
            default:
                throw new IllegalArgumentException("Bad source:" + source);
        }
    }

    protected void startLoading() {
        swipeRefreshLayout.setRefreshing(true);
        final LoadingCacheManager cache = getCache(onCreateLoaderSource());
        // From cache.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final Collection<DataRecord> dataRecords = cache.get(getDataType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callLoadFinish(dataRecords);
                    }
                });
            }
        };
        SharedExecutor.execute(r);
    }

    protected void callLoadFinish(Collection<DataRecord> dataRecords) {
        swipeRefreshLayout.setRefreshing(false);
        onLoadFinish(dataRecords);
    }

    protected LoaderSource onCreateLoaderSource() {
        return loaderSourceProvider.onRequestLoaderSource();
    }

    protected void onLoadFinish(Collection<DataRecord> collection) {
        getAdapter().update(collection);
    }

    abstract DataCategory getDataType();

    abstract CommonListAdapter onCreateAdapter();

    private void setupView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        listStateListener.onIdle();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        listStateListener.onScroll();
                        break;
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClick();
            }
        });

        buildFabIntro();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoading();
            }
        });
    }

    private void buildFabIntro() {
        new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(R.string.fab_intro_list))
                .setShape(ShapeType.CIRCLE)
                .setTarget(fab)
                // Always show when in dev mode.
                .setUsageId("data_list_viewer")
                .show();
    }

    protected void onFabClick() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_with_fab_template, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.data_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_selection:
                getAdapter().selectAll(false);
                return true;
            case R.id.action_select_all:
                getAdapter().selectAll(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupView();
        startLoading();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ArrayList<DataRecord> dataRecords = (ArrayList<DataRecord>) getAdapter().getDataRecords();
        DataCategory category = getDataType();
        Event event = Event.builder().arg1(category.ordinal()).obj(dataRecords).eventType(IntentEvents.EVENT_ON_CATEGORY_OF_DATA_SELECT_COMPLETE).build();
        EventBus.from(getContext()).publish(event);
    }

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    public interface ListStateListener {
        void onScroll();

        void onIdle();
    }
}
