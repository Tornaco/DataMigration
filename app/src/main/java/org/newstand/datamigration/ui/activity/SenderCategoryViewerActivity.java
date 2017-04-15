package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.CallInMainThread;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/23 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SenderCategoryViewerActivity extends AndroidCategoryViewerActivity {

    String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHost = getIntent().getStringExtra("host");
    }

    @Override
    public void onSubmit() {
        Intent intent = new Intent(this, DataSenderActivity.class);
        intent.putExtra("host", mHost);
        transitionTo(intent);
    }

    @ReceiverMethod
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        Logger.d("onTransportComplete %s", event);
        Session session = (Session) event.getObj();
        if (onRequestLoaderSource().getSession().equals(session)) {
            Logger.d("Matched session %s", session);
            finishWithAfterTransition();
            EventBus.from(this).unSubscribe(this);
        }
    }
}
