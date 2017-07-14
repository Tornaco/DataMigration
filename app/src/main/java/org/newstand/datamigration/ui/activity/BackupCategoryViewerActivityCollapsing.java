package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.BackupCategoryViewerFragment;
import org.newstand.datamigration.ui.widget.AppBarStateChangeListener;
import org.newstand.datamigration.worker.transport.Session;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.CallInMainThread;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupCategoryViewerActivityCollapsing extends CategoryViewerActivityCollapsing {

    protected LoaderSource mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resolveIntent();
        super.onCreate(savedInstanceState);
        showHomeAsUp();
    }

    @Override
    public void onAppBarLayoutStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
        super.onAppBarLayoutStateChanged(appBarLayout, state);
        if (state == AppBarStateChangeListener.State.EXPANDED || state == AppBarStateChangeListener.State.IDLE) {
            if (isLoadingComplete()) {
                getCollapsingToolbarLayout().setTitle(getString(R.string.oc_storage_backup,
                        org.newstand.datamigration.utils.Files.formatSize(loadedRecordsFileSize)));
            } else {
                getCollapsingToolbarLayout().setTitle(mSource.getSession().getName());
            }
        } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
            getCollapsingToolbarLayout().setTitle(mSource.getSession().getName());
        }
    }

    @Override
    protected int getFabIntro() {
        return R.string.fab_intro_category_restore;
    }

    @Override
    LoadingCacheManager getCache() {
        return LoadingCacheManager.bk();
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        Intent intent = new Intent(this, DataImportActivity.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, mSource);
        transitionTo(intent);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        Preconditions.checkNotNull(mSource);
        setTitle(mSource.getSession().getName());
        LoadingCacheManager.createBK(getApplicationContext(), mSource.getSession());
        EventBus.from(this).subscribe(this);
    }

    @ReceiverMethod
    @Keep
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        Session session = (Session) event.getObj();
        if (onRequestLoaderSource().getSession().equals(session)) {
            finishWithAfterTransition();
            EventBus.from(this).unSubscribe(this);
        }
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return mSource;
    }

    @Override
    protected Class<? extends Activity> getListHostActivityClz() {
        return BackupDataListHostActivity.class;
    }

    @Override
    protected Fragment onCreateViewerFragment() {
        return new BackupCategoryViewerFragment();
    }
}
