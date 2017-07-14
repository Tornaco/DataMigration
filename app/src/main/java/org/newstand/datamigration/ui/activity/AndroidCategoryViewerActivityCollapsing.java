package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.CategoryRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.fragment.AndroidCategoryViewerFragment;
import org.newstand.datamigration.ui.widget.AppBarStateChangeListener;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;

import java.util.ArrayList;
import java.util.List;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.CallInMainThread;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AndroidCategoryViewerActivityCollapsing extends CategoryViewerActivityCollapsing {
    private String staticTitle;

    @Getter
    protected long selectedRecordsFileSize = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LoadingCacheManager.droid() == null) {
            LoadingCacheManager.createDroid(getApplicationContext());
        }

        EventBus.from(this).subscribe(this);
        this.staticTitle = (String) getTitle();
        super.onCreate(savedInstanceState);
        showHomeAsUp();
    }

    @Override
    public void onAppBarLayoutStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
        super.onAppBarLayoutStateChanged(appBarLayout, state);
        if (state == AppBarStateChangeListener.State.EXPANDED || state == AppBarStateChangeListener.State.IDLE) {
            if (isLoadingComplete()) {
                updateCollapsingToolbarTextExpanded();
            } else {
                getCollapsingToolbarLayout().setTitle(staticTitle);
            }
        } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
            getCollapsingToolbarLayout().setTitle(staticTitle);
        }
    }

    @Override
    protected void updateSelectionCount(DataCategory category, final List<DataRecord> dataRecords) {
        super.updateSelectionCount(category, dataRecords);

        // Calculate size.
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DataRecord> categoryRecords = new ArrayList<>();
                categoryRecords.addAll(getAdapter().getDataRecords());
                // Reset size.
                selectedRecordsFileSize = 0;
                Collections.consumeRemaining(categoryRecords, new Consumer<DataRecord>() {
                    @Override
                    public void accept(@NonNull DataRecord dataRecord) {
                        CategoryRecord categoryRecord = (CategoryRecord) dataRecord;
                        DataCategory dataCategory = categoryRecord.category();
                        Collections.consumeRemaining(getCache().checked(dataCategory), new Consumer<DataRecord>() {
                            @Override
                            public void accept(@NonNull DataRecord dataRecord) {
                                if (dataRecord instanceof FileBasedRecord) {
                                    selectedRecordsFileSize += ((FileBasedRecord) dataRecord).getSize();
                                }
                            }
                        });

                        if (getAppBarState() != AppBarStateChangeListener.State.COLLAPSED) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateCollapsingToolbarTextExpanded();
                                }
                            });
                        }
                    }
                });

            }
        });

    }

    protected void updateCollapsingToolbarTextExpanded() {
        getCollapsingToolbarLayout().setTitle(getString(R.string.oc_storage_android,
                org.newstand.datamigration.utils.Files.formatSize(selectedRecordsFileSize)));
    }

    @Override
    protected int getFabIntro() {
        return R.string.fab_intro_category_backup;
    }

    @Override
    LoadingCacheManager getCache() {
        return LoadingCacheManager.droid();
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        transitionTo(new Intent(this, DataExportActivity.class));
    }

    @ReceiverMethod
    @Keep
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        finishWithAfterTransition();
        EventBus.from(this).unSubscribe(this);
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return LoaderSource.builder().session(Session.create()).parent(LoaderSource.Parent.Android).build();
    }

    @Override
    protected Class<? extends Activity> getListHostActivityClz() {
        return AndroidDataListHostActivity.class;
    }

    @Override
    protected Fragment onCreateViewerFragment() {
        return new AndroidCategoryViewerFragment();
    }
}
