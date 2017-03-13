package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.CategoryRecord;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.data.event.EventDefinations;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
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

public class CategorySelectionViewerFragment extends Fragment {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setSelectListener((OnCategorySelectListener) getActivity());
        setOnSubmitListener((OnSubmitListener) getActivity());
        EventBus.from(getContext()).subscribe(this);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
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
        final List<DataRecord> mokes = new ArrayList<>();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory type) {
                CategoryRecord dr = new CategoryRecord();
                dr.setId(type.name());
                dr.setDisplayName(type.name());
                dr.setCategory(type);
                dr.setSummary(getString(R.string.summary_category_viewer_need_click));
                mokes.add(dr);
            }
        });

        getAdapter().update(mokes);
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

    @ReceiverMethod
    @Events(EventDefinations.ON_CATEGORY_OF_DATA_SELECT_COMPLETE)
    @CallInMainThread
    public void updateSelectionCount(Event event) {
        Bundle data = event.getData();
        List<DataRecord> dataRecords = data.getParcelableArrayList(EventDefinations.KEY_CATEGORY_DATA_LIST);
        DataCategory category = DataCategory.valueOf(DataCategory.class, Preconditions.checkNotNull(data.getString(EventDefinations.KEY_CATEGORY)));
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
}
