package org.newstand.datamigration.service.schedule;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:10
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SchedulerServiceV21 extends JobService {

    private static final AtomicInteger JOB_ID = new AtomicInteger(0);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean schedule(Context context, Condition condition, ScheduleAction scheduleAction) {
        Logger.d("Scheduling with condition %s %s", condition, scheduleAction);
        Preconditions.checkNotNull(scheduleAction.getActionType());
        Preconditions.checkNotNull(scheduleAction.getSettings());
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        final JobInfo.Builder builder = new JobInfo.Builder(JOB_ID.incrementAndGet(),
                new ComponentName(context.getPackageName(), SchedulerServiceV21.class.getName()));
        builder.setRequiresCharging(condition.isRequiresCharging())
                .setPersisted(condition.isPersisted())
                .setRequiredNetworkType(condition.getNetworkType())
                .setRequiresDeviceIdle(condition.isRequiresDeviceIdle());
        if (condition.getTriggerContentUris() != null)
            Collections.consumeRemaining(condition.getTriggerContentUris(), new Consumer<String>() {
                @Override
                public void accept(@NonNull String uri) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        builder.addTriggerContentUri(new JobInfo.TriggerContentUri(Uri.parse(uri),
                                JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
                    }
                }
            });
        builder.setExtras(scheduleAction.toPersistBundle());
        return jobScheduler.schedule(builder.build()) == JOB_ID.get();
    }

    @Override
    public boolean onStartJob(final JobParameters params) {

        PersistableBundle data = params.getExtras();

        if (data == null) return true;

        final ScheduleAction scheduleAction = ScheduleAction.fromBundle(data);

        Logger.d("onStartJob %s", scheduleAction);

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                scheduleAction.execute(getApplicationContext());
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.d("onStopJob");
        return true;
    }
}