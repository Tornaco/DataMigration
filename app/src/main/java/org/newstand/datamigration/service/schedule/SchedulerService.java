package org.newstand.datamigration.service.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.repo.SchedulerParamRepoService;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.NetworkUtils;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:10
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SchedulerService extends Service {

    private static final int REQUEST_CODE_SCHEDULE = 0x1;

    private ServiceStub mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new ServiceStub();
        scheduleAllPersisted();
    }

    private void scheduleAllPersisted() {
        Collections.consumeRemaining(SchedulerParamRepoService.get().findAll(this),
                new Consumer<SchedulerParam>() {
                    @Override
                    public void accept(@NonNull SchedulerParam schedulerParam) {
                        if (schedulerParam.getCondition().isPersisted()) {
                            schedule(schedulerParam.getCondition(), schedulerParam.getAction());
                        }
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            return START_STICKY;
        }

        switch (action) {
            case IntentEvents.ACTION_SCHEDULE_TASK:
                onSchedule(intent.getLongExtra(IntentEvents.KEY_ACTION_SCHEDULE_TASK, -1 /*Invalid*/));
                break;
        }

        return START_STICKY;
    }

    private void schedule(Condition condition, ScheduleAction action) {

        // Assign id if necessary.
        if (action.getId() <= 0) {
            int size = SchedulerParamRepoService.get().findAll(this).size();
            action.setId(size + 1);
        }

        Logger.v("Schedule %s %s", condition, action);

        // Replace
        SchedulerParam exist = SchedulerParamRepoService.get().findById(this, action.getId());
        if (exist != null) {
            SchedulerParamRepoService.get().delete(this, exist);
        }

        // Save to repo.
        SchedulerParamRepoService.get().insert(this, new SchedulerParam(condition, action));

        Intent intent = new Intent(IntentEvents.ACTION_SCHEDULE_TASK);
        intent.putExtra(IntentEvents.KEY_ACTION_SCHEDULE_TASK, action.getId());
        PendingIntent pendingIntent = PendingIntent.getService(this,
                REQUEST_CODE_SCHEDULE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (condition.isRepeat())
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, condition.getTriggerAtMills(),
                    Interval.Day.getIntervalMills(), pendingIntent);
        else
            alarm.set(AlarmManager.RTC_WAKEUP, condition.getTriggerAtMills(), pendingIntent);
    }

    @SuppressWarnings("unchecked")
    private void onSchedule(long id) {
        Logger.d("onSchedule id %s", id);

        // Query task.
        final SchedulerParam param = SchedulerParamRepoService.get().findById(this, id);

        if (param == null) {
            Logger.e("No such param with id %s", id);
            return;
        }

        Logger.d("Now scheduling %s", param);

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (isInCondition(param.getCondition())) {
                    param.getAction().execute(getApplicationContext());
                }
            }
        });

        SchedulerParamRepoService.get().delete(this, param);

        // We should update the trigger time with an interval.
        if (param.getCondition().isRepeat()) {
            param.getCondition().setTriggerAtMills(param.getCondition().getTriggerAtMills()
                    + Interval.Day.getIntervalMills());

            SchedulerParamRepoService.get().insert(this, param);
        }
    }

    private boolean isInCondition(Condition condition) {
        if (condition.isRequiresCharging()) {
            boolean isCharging = BatteryStatusRetriever.isCharging(this);
            if (!isCharging) {
                Logger.i("Not charging, ignored.");
                return false;
            }
        }

        if (condition.isRequiresDeviceIdle()) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!pm.isDeviceIdleMode()) {
                    Logger.i("Not idle, ignored.");
                    return false;
                }
            }
        }

        int netType = condition.getNetworkType();

        if (netType == NetworkType.NETWORK_TYPE_UNMETERED.ordinal()) {
            if (!NetworkUtils.isInWifi(this)) {
                Logger.i("Not in wifi, ignored.");
                return false;
            }
        }

        return true;
    }

    private class ServiceStub extends Binder implements Scheduler {

        @Override
        public void watch(ServiceWatcher watcher) {

        }

        @Override
        public void schedule(Condition condition, ScheduleAction action) {
            SchedulerService.this.schedule(condition, action);
        }

        @Override
        public void unWatch(ServiceWatcher watcher) {

        }
    }
}