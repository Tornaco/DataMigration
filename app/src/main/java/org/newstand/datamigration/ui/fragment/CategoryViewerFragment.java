package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vlonjatg.progressactivity.ProgressRelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.CategoryRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.CallInMainThread;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 18:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CategoryViewerFragment extends TransitionSafeFragment {
    @Getter
    RecyclerView recyclerView;

    @Getter
    private CommonListAdapter adapter;

    @Setter
    private OnCategorySelectListener selectListener;

    @Setter
    private OnSubmitListener onSubmitListener;

    @Getter
    private FloatingActionButton fab;

    @Getter
    private ProgressRelativeLayout progressRelativeLayout;

    @Setter
    @Getter
    private LoaderSourceProvider loaderSourceProvider;

    private final List<DataRecord> mokes = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setSelectListener((OnCategorySelectListener) getActivity());
        setOnSubmitListener((OnSubmitListener) getActivity());
        setLoaderSourceProvider((LoaderSourceProvider) getActivity());
        EventBus.from(getContext()).subscribe(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.recycler_view_with_fab_template, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        progressRelativeLayout = (ProgressRelativeLayout) root.findViewById(R.id.progressLayout);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
    }

    private void startLoading() {
        progressRelativeLayout.showLoading();
        final LoadingCacheManager cache = loaderSourceProvider.onRequestLoaderSource().getParent()
                == LoaderSource.Parent.Android
                ? LoadingCacheManager.droid() : LoadingCacheManager.bk();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DataCategory.consumeAll(new Consumer<DataCategory>() {
                    @Override
                    public void consume(@NonNull DataCategory category) {

                        if (!isAlive()) return;

                        Collection<DataRecord> records = cache.get(category);
                        if (isAlive() && !Collections.isEmpty(records)) {

                            int total = records.size();
                            final int[] sel = {0};

                            Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                                @Override
                                public void consume(@NonNull DataRecord dataRecord) {
                                    if (dataRecord.isChecked())
                                        sel[0]++;
                                }
                            });

                            CategoryRecord dr = new CategoryRecord();
                            dr.setId(category.name());
                            dr.setDisplayName(getStringSafety(category.nameRes()));
                            dr.setCategory(category);
                            dr.setSummary(buildSelectionSummary(total, sel[0]));
                            mokes.add(dr);

                            onLoadComplete();// FIXME Another m name?
                        }
                    }
                });
                onLoadComplete();
            }
        };
        SharedExecutor.execute(r);
    }

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

    private void onFabClick() {
        onSubmitListener.onSubmit();
    }

    private CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getActivity()) {
            @Override
            protected void onBindViewHolder(CommonListViewHolder holder, DataRecord r) {
                super.onBindViewHolder(holder, r);
                CategoryRecord cr = (CategoryRecord) r;
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(),
                        cr.getCategory().iconRes()));
                holder.getLineTwoTextView().setText(cr.getSummary());
            }

            @Override
            protected void onItemClick(CommonListViewHolder holder) {
                CategoryRecord cr = (CategoryRecord) getAdapter().getDataRecords().get(holder.getAdapterPosition());
                onCategorySelect(cr);
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startLoading();
    }

    private void onLoadComplete() {
        if (isAlive()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAdapter().update(mokes);
                    progressRelativeLayout.showContent();
                }
            });
        }
    }

    private void updateSelectionCount(final DataCategory category, List<DataRecord> dataRecords) {

        final int total = dataRecords.size();
        final AtomicInteger selected = new AtomicInteger(0);
        Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
            @Override
            public void consume(@NonNull DataRecord record) {
                if (record.isChecked()) {
                    selected.incrementAndGet();
                }
            }
        });

        List<DataRecord> records = getAdapter().getDataRecords();
        Collections.consumeRemaining(records, new Consumer<DataRecord>() {
            @Override
            public void consume(@NonNull DataRecord record) {
                CategoryRecord categoryRecord = (CategoryRecord) record;
                if (categoryRecord.getCategory() == category) {
                    categoryRecord.setSummary(buildSelectionSummary(total, selected.get()));
                }
            }
        });
        getAdapter().onUpdate();
    }

    private String buildSelectionSummary(int total, int selectionCnt) {
        if (isDetached()) return null;
        return getString(R.string.summary_category_viewer, String.valueOf(total), String.valueOf(selectionCnt));
    }

    @SuppressWarnings("unchecked")
    @ReceiverMethod
    @Events(IntentEvents.ON_CATEGORY_OF_DATA_SELECT_COMPLETE)
    @CallInMainThread
    public void updateSelectionCount(Event event) {
        List<DataRecord> dataRecords = (List<DataRecord>) event.getObj();
        DataCategory category = DataCategory.fromInt(event.getArg1());
        updateSelectionCount(category, dataRecords);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.from(getContext()).unSubscribe(this);
    }

    private void onCategorySelect(CategoryRecord cr) {
        selectListener.onCategorySelect(cr.getCategory());
    }

    public interface OnCategorySelectListener {
        void onCategorySelect(DataCategory category);
    }

    public interface OnSubmitListener {
        void onSubmit();
    }

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }
}
