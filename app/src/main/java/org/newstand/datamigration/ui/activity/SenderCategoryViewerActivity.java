package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.fragment.SenderCategoryViewerFragment;

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

public class SenderCategoryViewerActivity extends AndroidCategoryViewerActivityCollapsing {

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
    @Keep
    @Events(IntentEvents.EVENT_TRANSPORT_COMPLETE)
    @CallInMainThread
    public void onTransportComplete(Event event) {
        finishWithAfterTransition();
        EventBus.from(this).unSubscribe(this);
    }

    @Override
    protected Fragment onCreateViewerFragment() {
        return new SenderCategoryViewerFragment();
    }

    @Override
    protected Class<? extends Activity> getListHostActivityClz() {
        return SenderDataListHostActivity.class;
    }

    @Override
    public boolean isLoadEnabledForCategory(DataCategory category) {
        if (category == DataCategory.CustomFile) return false;
        if (category == DataCategory.Alarm) return false;
        if (category == DataCategory.SystemApp) return false;
        if (category == DataCategory.SystemSettings) return false;
        if (category == DataCategory.Wifi) return false;
        return super.isLoadEnabledForCategory(category);
    }
}
