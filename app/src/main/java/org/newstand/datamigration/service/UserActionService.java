package org.newstand.datamigration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.repo.UserActionRepoService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.annotation.Events;
import dev.nick.eventbus.annotation.ReceiverMethod;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionService extends Service {

    private ImplStub mStub;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStub = new ImplStub();
        EventBus.from(this).subscribe(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.from(this).unSubscribe(this);
    }

    @ReceiverMethod
    @Events(IntentEvents.EVENT_ON_USER_ACTION)
    @WorkerThread
    public void handleUserAction(Event e) {
        mStub.onUserAction((UserAction) e.getObj());
    }

    private class ImplStub extends Binder implements UserActionHandler {

        private ExecutorService mSingleThreadPool;

        ImplStub() {
            mSingleThreadPool = Executors.newSingleThreadExecutor();
        }

        @Override
        public void onUserAction(@NonNull final UserAction action) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    UserActionRepoService.get().insert(action);
                }
            };
            mSingleThreadPool.execute(r);
        }

        @NonNull
        @Override
        public List<UserAction> getAll() {
            return UserActionRepoService.get().findAll();
        }

        @NonNull
        @Override
        public List<UserAction> getByFingerPrint(long finger) {
            return UserActionRepoService.get().findByFingerPrint(finger);
        }
    }
}
