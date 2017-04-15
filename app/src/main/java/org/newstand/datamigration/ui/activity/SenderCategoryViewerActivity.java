package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.newstand.datamigration.data.event.IntentEvents;

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

    private String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHost = getIntent().getStringExtra(IntentEvents.KEY_HOST);
    }

    @Override
    public void onSubmit() {
        Intent intent = new Intent(this, DataSenderActivity.class);
        intent.putExtra(IntentEvents.KEY_HOST, mHost);
        transitionTo(intent);
    }

    @ReceiverMethod
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        finishWithAfterTransition();
        EventBus.from(this).unSubscribe(this);
    }
}
