package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.norbsoft.typefacehelper.TypefaceHelper;
import com.orhanobut.logger.Logger;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListenerMainThreadAdapter;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.data.event.EventDefinations;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.service.DataSelectionKeeperServiceProxy;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 14:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class DataListViewerFragment extends Fragment {

    @Getter
    RecyclerView recyclerView;

    @Getter
    FloatingActionButton fab;

    @Getter
    private CommonListAdapter adapter;

    @Getter
    private DataLoaderManager dataLoaderManager;

    @Getter
    private LoaderSourceProvider loaderSourceProvider;

    @Getter
    private ListStateListener listStateListener;

    @Getter
    private ProgressRelativeLayout progressLayout;

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
    }

    private void loadFresh() {
        dataLoaderManager = DataLoaderManager.from(getContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataLoaderManager.loadAsync(onCreateLoaderSource(), getDataType(),
                        new LoaderListenerMainThreadAdapter<DataRecord>() {
                            @Override
                            public void onCompleteMainThread(Collection<DataRecord> collection) {
                                super.onCompleteMainThread(collection);
                                Logger.d("onCompleteMainThread~");
                                callLoadFinish(collection);
                            }
                        });
            }
        });
    }

    private void startLoading() {
        progressLayout.showLoading();
        // From cache.
        DataSelectionKeeperServiceProxy.getSelectionByCategoryAsync(getActivity(), getDataType(),
                new ActionListenerMainThreadAdapter<List<DataRecord>>(Looper.getMainLooper()) {
                    @Override
                    public void onActionMainThread(@Nullable List<DataRecord> dataRecords) {
                        if (!Collections.isEmpty(dataRecords)) {
                            callLoadFinish(dataRecords);
                            return;
                        }
                        // From new...
                        loadFresh();
                    }
                });
    }

    private void callLoadFinish(Collection<DataRecord> dataRecords) {
        boolean isEmpty = Collections.isEmpty(dataRecords);
        if (isEmpty)
            progressLayout.showEmpty(R.drawable.ic_mood_bad_white, getString(R.string.empty_title), getString(R.string.empty_summary));
        else
            progressLayout.showContent();
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
    }

    protected void onFabClick() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_with_fab_template, container, false);
        TypefaceHelper.typeface(root);
        progressLayout = (ProgressRelativeLayout) root;
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        return root;
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
        Bundle data = new Bundle();
        data.putParcelableArrayList(EventDefinations.KEY_CATEGORY_DATA_LIST, dataRecords);
        data.putString(EventDefinations.KEY_CATEGORY, category.name());
        Event event = new Event(EventDefinations.ON_CATEGORY_OF_DATA_SELECT_COMPLETE, data);
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
