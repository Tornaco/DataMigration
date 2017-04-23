package org.newstand.datamigration.service.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import org.newstand.datamigration.service.ServiceProxy;

/**
 * Created by Nick@NewStand.org on 2017/4/23 20:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SchedulerServiceProxy extends ServiceProxy implements Scheduler {

    private Scheduler scheduler;

    private SchedulerServiceProxy(Context context) {
        super(context, new Intent(context, SchedulerService.class));
    }

    @Override
    public void onConnected(IBinder binder) {
        scheduler = (Scheduler) binder;
    }

    @Override
    public void watch(final ServiceWatcher watcher) {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                scheduler.watch(watcher);
            }
        });
    }

    @Override
    public void schedule(final Condition condition, final ScheduleAction action) {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                scheduler.schedule(condition, action);
            }
        });
    }

    @Override
    public void unWatch(final ServiceWatcher watcher) {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                scheduler.unWatch(watcher);
            }
        });
    }

    public static void start(Context context) {
        context.startService(new Intent(context, SchedulerService.class));
    }

    public static void schedule(Context context, final Condition condition, final ScheduleAction action) {
        new SchedulerServiceProxy(context).schedule(condition, action);
    }

    public void watch(Context context, final ServiceWatcher watcher) {
        new SchedulerServiceProxy(context).watch(watcher);
    }

    public void unWatch(Context context, final ServiceWatcher watcher) {
        new SchedulerServiceProxy(context).unWatch(watcher);
    }
}
