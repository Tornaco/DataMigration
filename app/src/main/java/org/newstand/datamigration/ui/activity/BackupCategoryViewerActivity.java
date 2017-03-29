package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.worker.backup.session.Session;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupCategoryViewerActivity extends CategoryViewerActivity {

    private LoaderSource mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        resolveIntent();
        showViewerFragment();
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

    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        Preconditions.checkNotNull(mSource);
        Logger.d("Source = " + mSource);
        setTitle(mSource.getSession().getName());
        LoadingCacheManager.createBK(getApplicationContext(), mSource.getSession());
        EventBus.from(this).subscribe(this);
    }

    @ReceiverMethod
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    public void onTransportComplete(Event event) {
        Logger.d("onTransportComplete %s", event);
        Session session = (Session) event.getObj();
        if (onRequestLoaderSource().getSession().equals(session)) {
            Logger.d("Matched session %s", session);
            finishWithAfterTransition();
            EventBus.from(this).unSubscribe(this);
        }
    }


    @Override
    public LoaderSource onRequestLoaderSource() {
        return mSource;
    }

}
