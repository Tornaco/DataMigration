package org.newstand.datamigration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.data.event.EventDefinations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSelectionKeeperService extends Service {

    private final Map<DataCategory, List<DataRecord>> mDataMap = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BinderStub();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.from(this).subscribe(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Events(EventDefinations.ON_CATEGORY_OF_DATA_SELECT_COMPLETE)
    @ReceiverMethod
    public void onSelection(Event event) {
        Bundle data = event.getData();
        List<DataRecord> dataRecords = data.getParcelableArrayList(EventDefinations.KEY_CATEGORY_DATA_LIST);
        DataCategory category = DataCategory.valueOf(DataCategory.class, Preconditions.checkNotNull(data.getString(EventDefinations.KEY_CATEGORY)));
        synchronized (mDataMap) {
            mDataMap.remove(category);
            mDataMap.put(category, dataRecords);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.from(this).unSubscribe(this);
        mDataMap.clear();
        Logger.d("onDestroy");
    }

    public class BinderStub extends Binder {
        public List<DataRecord> getSelectionByCategory(DataCategory category) {
            List<DataRecord> out = new ArrayList<>();
            List<DataRecord> now = mDataMap.get(category);
            if (now == null) return out;
            out.addAll(now);
            return out;
        }
    }
}
