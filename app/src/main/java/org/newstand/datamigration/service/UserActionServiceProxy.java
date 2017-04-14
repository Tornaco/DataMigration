package org.newstand.datamigration.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.UserAction;

import java.util.ArrayList;
import java.util.List;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionServiceProxy extends ServiceProxy implements UserActionHandler {

    private UserActionHandler handler;

    public UserActionServiceProxy(Context context) {
        super(context, new Intent(context, UserActionService.class));
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, UserActionService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, UserActionService.class));
    }

    public static void publishNewAction(String title, String summary) {
        publishNewAction(UserAction.builder()
                .date(System.currentTimeMillis())
                .fingerPrint(System.currentTimeMillis())
                .eventTitle(title)
                .eventDescription(summary)
                .build());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void publishNewAction(@NonNull UserAction action) {
        Preconditions.checkNotNull(action, "Null action!");
        EventBus.from(null).publish(Event.builder().eventType(IntentEvents.EVENT_ON_USER_ACTION).obj(action).build());
    }

    public static List<UserAction> getAll(@NonNull Context context) {
        return new UserActionServiceProxy(context).getAll();
    }

    public static List<UserAction> getByFingerPrint(@NonNull Context context, long finger) {
        return new UserActionServiceProxy(context).getByFingerPrint(finger);
    }

    @Override
    public void onConnected(IBinder binder) {
        handler = (UserActionHandler) binder;
    }

    @Override
    public void onUserAction(@NonNull UserAction action) {
        publishNewAction(action);
    }

    @NonNull
    @Override
    public List<UserAction> getAll() {
        final List<UserAction> res = new ArrayList<>();
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                res.addAll(handler.getAll());
            }
        }, "getAll");
        waitForCompletion();
        return res;
    }

    @NonNull
    @Override
    public List<UserAction> getByFingerPrint(final long fingerPrint) {
        final List<UserAction> res = new ArrayList<>();
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                res.addAll(handler.getByFingerPrint(fingerPrint));
            }
        }, "getByFingerPrint:" + fingerPrint);
        waitForCompletion();
        return res;
    }
}
