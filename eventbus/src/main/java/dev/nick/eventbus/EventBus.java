/*
 * Copyright (c) 2016 Nick Guo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nick.eventbus;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

import dev.nick.eventbus.internal.EventsWirer;
import dev.nick.eventbus.internal.PublisherService;
import dev.nick.eventbus.utils.Preconditions;

/**
 * Created by nick on 16-4-1.
 * Email: nick.guo.dev@icloud.com
 */
public class EventBus {

    private static final String LOG_TAG = "EventBus";

    public static boolean DEBUG = false;

    private static EventBus sBus;
    private PublisherService mService;
    private Handler mHandler;
    private EventsWirer mWirer;

    private EventBus() {
        mService = new PublisherService();

        HandlerThread handlerThread = new HandlerThread("event_bus");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        mWirer = new EventsWirer(mService);
        log("Event bus created!");
    }

    private synchronized static EventBus create(Context context) {
        if (sBus == null) sBus = new EventBus();
        return sBus;
    }

    public static EventBus from(Context context) {
        return create(context);
    }

    public void publish(@NonNull final Event event) {
        log("publish:" + event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mService.publish(Preconditions.checkNotNull(event));
            }
        });
    }

    public void publishEmptyEvent(final int... events) {
        log("publishEmptyEvent:" + Arrays.toString(events));
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int e : events) {
                    publish(new Event(e));
                }
            }
        });
    }

    public void subscribe(@NonNull final EventReceiver receiver) {
        subscribeBinder(receiver);
    }

    private void subscribeBinder(@NonNull final IEventReceiver receiver) {
        log("subscribe:" + receiver);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mService.subscribe(Preconditions.checkNotNull(receiver));
            }
        });
    }

    public void subscribe(@NonNull final Object object) {
        log("subscribe:" + object);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWirer.wire(Preconditions.checkNotNull(object));
            }
        });
    }

    public void unSubscribe(@NonNull final EventReceiver receiver) {
        log("unSubscribe:" + receiver);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mService.unSubscribe(Preconditions.checkNotNull(receiver));
            }
        });
    }

    public void unSubscribe(@NonNull final Object object) {
        log("unSubscribe:" + object);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWirer.unWire(object);
            }
        });
    }

    private void log(Object message) {
        if (DEBUG) Log.d(LOG_TAG, String.valueOf(message));
    }

    public IBinder generateStub() {
        return new IEventBus.Stub() {

            @Override
            public void publish(Event event) throws RemoteException {
                EventBus.this.publish(event);
            }

            @Override
            public void publishEmptyEvent(int event) throws RemoteException {
                EventBus.this.publishEmptyEvent(event);
            }

            @Override
            public void subscribe(IEventReceiver receiver) throws RemoteException {
                EventBus.this.subscribeBinder(receiver);
            }

            @Override
            public void unSubscribe(IEventReceiver receiver) throws RemoteException {
                EventBus.this.unSubscribe(receiver);
            }
        };
    }
}
