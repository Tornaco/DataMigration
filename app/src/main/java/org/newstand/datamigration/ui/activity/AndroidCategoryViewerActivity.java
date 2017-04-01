package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

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

public class AndroidCategoryViewerActivity extends CategoryViewerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        if (LoadingCacheManager.droid() == null) {
            LoadingCacheManager.createDroid(getApplicationContext());
        }
        EventBus.from(this).subscribe(this);
        showViewerFragment();
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
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        Logger.d("onTransportComplete %s", event);
        finishWithAfterTransition();
        EventBus.from(this).unSubscribe(this);
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return LoaderSource.builder().session(Session.create()).parent(LoaderSource.Parent.Android).build();
    }
}
