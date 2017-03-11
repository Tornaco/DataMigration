package org.newstand.datamigration.ui.fragment;

import android.app.ProgressDialog;
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

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListenerMainThreadAdapter;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.model.message.EventDefinations;
import org.newstand.datamigration.service.DataSelectionKeeperServiceProxy;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;
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
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loaderSourceProvider = (LoaderSourceProvider) getActivity();
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
                                callLoadFinish(collection);
                            }
                        });
            }
        });
    }

    private void startLoading() {
        progressDialog = ProgressDialogCompat.createUnCancelableIndeterminate(getActivity());
        progressDialog.show();
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
        progressDialog.dismiss();
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
}
