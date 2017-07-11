package org.newstand.datamigration.ui.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.CategoryRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.ui.widget.PermissionMissingDialog;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.EventReceiver;
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
    private RecyclerView recyclerView;

    @Getter
    private CommonListAdapter adapter;

    @Setter
    private OnCategorySelectListener selectListener;

    @Setter
    private OnSubmitListener onSubmitListener;

    @Getter
    private FloatingActionButton fab;

    @Setter
    @Getter
    private LoaderSourceProvider loaderSourceProvider;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    private final List<DataRecord> mokes = new ArrayList<>();

    private CountDownLatch loadingLatch;

    private EventReceiver selectionEventReceiver = new EventReceiver() {
        @Override
        public void onReceive(@NonNull Event event) {
            updateSelectionCount(event);
        }

        @Override
        public int[] events() {
            return new int[]{IntentEvents.EVENT_ON_CATEGORY_OF_DATA_SELECT_COMPLETE};
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setSelectListener((OnCategorySelectListener) getActivity());
        setOnSubmitListener((OnSubmitListener) getActivity());
        setLoaderSourceProvider((LoaderSourceProvider) getActivity());

        // Register events manually~
        EventBus.from(getContext()).subscribe(selectionEventReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.category_viewer, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        startLoadingChecked();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startLoadingChecked() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.READ_SMS)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            startLoading();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }

    private void onPermissionNotGrant() {
        PermissionMissingDialog.attach(getActivity());
    }

    private LoadingCacheManager getCacheManager(LoaderSource.Parent parent) {
        switch (parent) {
            case Android:
                return LoadingCacheManager.droid();
            case Received:
                return LoadingCacheManager.received();
            case Backup:
                return LoadingCacheManager.bk();
            default:
                throw new IllegalArgumentException("Bad parent:" + parent);
        }
    }

    private void startLoading() {
        waitForAllLoader();
        LoaderSource.Parent parent = loaderSourceProvider.onRequestLoaderSource().getParent();
        final LoadingCacheManager cache = getCacheManager(parent);

        Logger.i("startLoading delegate parent %s, cache %s", loaderSourceProvider.onRequestLoaderSource().getParent(), cache);

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull final DataCategory category) {
                if (!SettingsProvider.isLoadEnabledForCategory(category)) {
                    if (loadingLatch != null && loadingLatch.getCount() > 0) {
                        loadingLatch.countDown();
                    }
                    return;
                }
                SharedExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAlive()) return;
                        cache.refresh(category);
                        if (!isAlive()) return;

                        Collection<DataRecord> records = cache.get(category);

                        if (loadingLatch != null && loadingLatch.getCount() > 0) {
                            loadingLatch.countDown();
                        }

                        if (isAlive() && !Collections.isNullOrEmpty(records)) {

                            int total = records.size();
                            final int[] sel = {0};

                            Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                                @Override
                                public void accept(@NonNull DataRecord dataRecord) {
                                    if (dataRecord.isChecked())
                                        sel[0]++;
                                }
                            });

                            CategoryRecord dr = new CategoryRecord();
                            dr.setId(category.name());
                            dr.setDisplayName(getStringSafety(category.nameRes()));
                            dr.setCategory(category);
                            dr.setSummary(buildSelectionSummary(total, sel[0]));
                            mokes.remove(dr);
                            mokes.add(dr);
                        }
                    }
                });
            }
        });
    }

    private void setupView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = onCreateAdapter();
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClick();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoadingChecked();
            }
        });
        showFab(false);
    }

    private void buildFabIntro() {
        if (isAlive()) new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(getFabIntro()))
                .setShape(ShapeType.CIRCLE)
                .setTarget(fab)
                // Always show when in dev mode.
                .setUsageId("intro_category_viewer_" + getClass().getName())
                .show();
    }

    protected
    @StringRes
    int getFabIntro() {
        return -1;
    }

    private void onFabClick() {
        onSubmitListener.onSubmit();
    }

    private CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getActivity()) {
            @Override
            protected void onBindViewHolder(CommonListViewHolder holder, DataRecord r) {
                CategoryRecord cr = (CategoryRecord) r;
                holder.getLineOneTextView().setText(r.getDisplayName());
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(),
                        cr.getCategory().iconRes()));
                holder.getLineTwoTextView().setText(cr.getSummary());
            }

            @Override
            protected void onItemClick(CommonListViewHolder holder) {
                int position = holder.getAdapterPosition();
                if (position < 0) {
                    // This is a workaround to fix the issue, when adapter position is -1.
                    // which means before/under init???
                    return;
                }
                CategoryRecord cr = (CategoryRecord) getAdapter().getDataRecords().get(position);
                onCategorySelect(cr);
            }
        };
    }

    private void onLoadComplete() {
        if (isAlive()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAdapter().update(mokes);
                }
            });
        }
    }

    private void waitForAllLoader() {
        swipeRefreshLayout.setRefreshing(true);
        loadingLatch = new CountDownLatch(DataCategory.values().length);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadingLatch.await();

                    if (isAlive()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                java.util.Collections.sort(mokes, new Comparator<DataRecord>() {
                                    @Override
                                    public int compare(DataRecord r1, DataRecord r2) {
                                        if (r1 == null || r2 == null) return 1;
                                        return r1.category().ordinal() < r2.category().ordinal() ? -1 : 1;
                                    }
                                });
                                onLoadComplete();// FIXME Another m name?
                            }
                        });
                    }
                } catch (InterruptedException ignored) {

                }
            }
        }).start();
    }

    private void updateSelectionCount(final DataCategory category, List<DataRecord> dataRecords) {

        final int total = dataRecords.size();
        final AtomicInteger selected = new AtomicInteger(0);
        Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord record) {
                if (record.isChecked()) {
                    selected.incrementAndGet();
                }
            }
        });

        List<DataRecord> records = getAdapter().getDataRecords();
        Collections.consumeRemaining(records, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord record) {
                CategoryRecord categoryRecord = (CategoryRecord) record;
                if (categoryRecord.getCategory() == category) {
                    categoryRecord.setSummary(buildSelectionSummary(total, selected.get()));
                    if (selected.get() > 0) categoryRecord.setChecked(true);
                    else categoryRecord.setChecked(false);
                }
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAdapter().onUpdate();
                showFab(getAdapter().hasSelection());
            }
        });
    }

    private void showFab(boolean show) {
        if (show) {
            fab.show();
            buildFabIntro();
        } else {
            fab.hide();
        }
    }

    private String buildSelectionSummary(int total, int selectionCnt) {
        if (!isAlive()) return null;
        return getString(R.string.summary_category_viewer, String.valueOf(selectionCnt), String.valueOf(total));
    }

    @SuppressWarnings("unchecked")
    @ReceiverMethod
    @Keep
    @Events(IntentEvents.EVENT_ON_CATEGORY_OF_DATA_SELECT_COMPLETE)
    public void updateSelectionCount(Event event) {
        List<DataRecord> dataRecords = (List<DataRecord>) event.getObj();
        DataCategory category = DataCategory.fromInt(event.getArg1());
        updateSelectionCount(category, dataRecords);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.from(getContext()).unSubscribe(selectionEventReceiver);
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
