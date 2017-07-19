package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.CategoryRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.fragment.CategoryViewerFragment;
import org.newstand.datamigration.ui.widget.AppBarStateChangeListener;
import org.newstand.datamigration.ui.widget.PermissionMissingDialog;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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

// With CollapsingToolbarLayout.
public abstract class CategoryViewerActivityCollapsing extends TransitionSafeActivity {

    @Getter
    private AppBarLayout appBarLayout;
    @Getter
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Getter
    private AppBarStateChangeListener.State appBarState = AppBarStateChangeListener.State.IDLE;

    private boolean hasEverExpandedToolbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getThemeColor()) {
            case White:
                setContentView(R.layout.scrollable_with_recycler_dark);
                break;
            default:
                setContentView(R.layout.scrollable_with_recycler);
                break;
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(false, false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                appBarState = state;
                onAppBarLayoutStateChanged(appBarLayout, state);
            }
        });

        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        showHomeAsUp();
        setTitle(getTitle());

        // Register events manually~
        EventBus.from(this).subscribe(selectionEventReceiver);

        setupView();
        showRetention();
    }

    public void onAppBarLayoutStateChanged(AppBarLayout appBarLayout,
                                           final AppBarStateChangeListener.State state) {
        // Delay for better ui.
        SharedExecutor.runOnUIThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (state == AppBarStateChangeListener.State.EXPANDED && getAdapter().hasSelection()) {
                    buildFabIntro();
                }
            }
        }, 1200);
    }

    @Override
    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppThemeNoActionBar(color);
        setTheme(themeRes);
    }

    protected void showViewerFragment() {
        replaceV4(R.id.container, onCreateViewerFragment(), null);
    }

    protected Fragment onCreateViewerFragment() {
        return new CategoryViewerFragment();
    }

    abstract LoadingCacheManager getCache();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCache().clear();
        EventBus.from(this).unSubscribe(selectionEventReceiver);
    }

    public void onCategorySelect(DataCategory category) {
        Intent intent = new Intent(this, getListHostActivityClz());
        intent.putExtra(IntentEvents.KEY_CATEGORY, category.name());
        intent.putExtra(IntentEvents.KEY_SOURCE, onRequestLoaderSource());
        transitionTo(intent);
    }

    protected abstract LoaderSource onRequestLoaderSource();

    protected Class<? extends Activity> getListHostActivityClz() {
        return DataListHostActivity.class;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onSubmit() {
        // Nothing.
    }

    @Getter
    private RecyclerView recyclerView;

    @Getter
    private SwipeRefreshLayout swipeRefreshLayout;

    @Getter
    private CommonListAdapter adapter;

    @Getter
    private FloatingActionButton fab;

    private final List<DataRecord> mokes = new ArrayList<>();

    @Getter
    protected long loadedRecordsFileSize = 0L;

    private LoaderState loaderState = LoaderState.IDLE;

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

    private void showRetention() {
        boolean hasBasicPermission = true;

        for (String perm : PERMISSIONS) {
            boolean hasOne = ContextCompat.checkSelfPermission(getApplicationContext(), perm) == PackageManager.PERMISSION_GRANTED;
            if (!hasOne) {
                hasBasicPermission = false;
                break;
            }
        }

        if (!hasBasicPermission) {
            new MaterialStyledDialog.Builder(this)
                    .setTitle(R.string.permission_request)
                    .setDescription(R.string.permission_request_message)
                    .setHeaderDrawable(R.drawable.photo_backup_help_card_header)
                    .withDarkerOverlay(false)
                    .setCancelable(false)
                    .setPositiveText(android.R.string.ok)
                    .setNegativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startLoadingChecked();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            startLoading();
        }
    }

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_SMS
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startLoadingChecked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(PERMISSIONS)
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
        PermissionMissingDialog.attach(this);
        startLoading();
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

    public boolean isLoadEnabledForCategory(DataCategory category) {
        return SettingsProvider.isLoadEnabledForCategory(category);
    }

    private void startLoading() {
        onStartLoading();
        LoaderSource.Parent parent = onRequestLoaderSource().getParent();
        final LoadingCacheManager cache = getCacheManager(parent);

        Logger.i("startLoading delegate parent %s, cache %s", onRequestLoaderSource().getParent(), cache);

        loadedRecordsFileSize = 0L;

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull final DataCategory category) {
                if (!isLoadEnabledForCategory(category)) {
                    return;
                }
                if (isDestroyedCompat()) return;
                cache.refresh(category);
                if (isDestroyedCompat()) return;

                Collection<DataRecord> records = cache.get(category);

                if (!isDestroyedCompat() && !Collections.isNullOrEmpty(records)) {

                    int total = records.size();
                    final int[] sel = {0};

                    Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                        @Override
                        public void accept(@NonNull DataRecord dataRecord) {
                            if (dataRecord instanceof FileBasedRecord) {
                                FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;
                                if (fileBasedRecord.getPath() != null) try {
                                    long pieceSize = fileBasedRecord.getSize();
                                    loadedRecordsFileSize += pieceSize;
                                } catch (Throwable e) {
                                    Logger.e(e, "Fail query file size");
                                }
                            }
                            if (dataRecord.isChecked())
                                sel[0]++;
                        }
                    });

                    CategoryRecord dr = new CategoryRecord();
                    dr.setId(category.name());
                    dr.setDisplayName(getString(category.nameRes()));
                    dr.setCategory(category);
                    dr.setSummary(buildSelectionSummary(total, sel[0]));
                    mokes.remove(dr);
                    mokes.add(dr);
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                postOnLoadComplete();
            }
        });
    }

    protected boolean isLoading() {
        return loaderState == LoaderState.LOADING;
    }

    protected boolean isLoadingComplete() {
        return loaderState == LoaderState.LOADED;
    }

    private void setupView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.polluted_waves));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
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
        showFab(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRetention();
            }
        });
    }

    private void buildFabIntro() {
        if (!isDestroyedCompat()) new MaterialIntroView.Builder(this)
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
        if (getAdapter().hasSelection()) {
            onSubmit();
        } else {
            Snackbar.make(getRecyclerView(), R.string.title_need_selection, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    private CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(this) {
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

    private void postAdapterUpdate() {
        if (!isDestroyedCompat()) {

            boolean empty = mokes.size() == 0;
            if (empty) onNoDataLoaded();

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAdapter().update(mokes);
                }
            });
        }
    }

    private void onNoDataLoaded() {
        Snackbar.make(getAppBarLayout(), R.string.empty_summary, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Empty for now.
                    }
                })
                .show();
    }

    private void onStartLoading() {
        loaderState = LoaderState.LOADING;
        swipeRefreshLayout.setRefreshing(true);
        appBarLayout.setExpanded(false, true);
    }

    private void postOnLoadComplete() {
        loaderState = LoaderState.LOADED;
        if (!isDestroyedCompat()) {
            runOnUiThread(new Runnable() {
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
                    postAdapterUpdate();
                }
            });
        }
    }

    protected void updateSelectionCount(final DataCategory category, List<DataRecord> dataRecords) {
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
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAdapter().onUpdate();
                boolean hasSelection = getAdapter().hasSelection();
                showFab(hasSelection);
                if (!hasEverExpandedToolbar && hasSelection) {
                    getAppBarLayout().setExpanded(true, true);
                    hasEverExpandedToolbar = true;
                }
            }
        });
    }

    private void showFab(boolean show) {
        Logger.v("showFab:%s", show);
        if (show) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    private String buildSelectionSummary(int total, int selectionCnt) {
        if (isDestroyedCompat()) return null;
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

    private void onCategorySelect(CategoryRecord cr) {
        onCategorySelect(cr.getCategory());
    }

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    private enum LoaderState {
        IDLE, LOADING, LOADED
    }
}

