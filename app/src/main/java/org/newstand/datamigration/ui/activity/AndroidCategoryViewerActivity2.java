package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.AndroidCategoryViewerFragment;
import org.newstand.datamigration.ui.widget.AppBarStateChangeListener;
import org.newstand.datamigration.worker.transport.Session;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.CallInMainThread;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AndroidCategoryViewerActivity2 extends CategoryViewerActivity2 {
    private String staticTitle;

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
                getCollapsingToolbarLayout().setTitle(getString(R.string.oc_storage,
                        org.newstand.datamigration.utils.Files.formatSize(fileSize)));
            } else {
                getCollapsingToolbarLayout().setTitle(staticTitle);
            }
        } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
            getCollapsingToolbarLayout().setTitle(staticTitle);
        }
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
