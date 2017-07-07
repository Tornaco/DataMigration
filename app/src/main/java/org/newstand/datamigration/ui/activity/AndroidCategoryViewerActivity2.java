package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.AndroidCategoryViewerFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LoadingCacheManager.droid() == null) {
            LoadingCacheManager.createDroid(getApplicationContext());
        }
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        EventBus.from(this).subscribe(this);
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
