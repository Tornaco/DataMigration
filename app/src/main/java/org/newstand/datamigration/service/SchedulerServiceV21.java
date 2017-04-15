package org.newstand.datamigration.service;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListenerAdapter;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
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

    public static boolean schedule(Context context) {
        Logger.d("scheduling");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID.incrementAndGet(),
                new ComponentName(context.getPackageName(), SchedulerServiceV21.class.getName()));
        builder.setRequiresCharging(true)
                // .setPeriodic(Interval.Minutes.getIntervalMills())
                .setPersisted(true)
                .setRequiresDeviceIdle(false);
        return jobScheduler.schedule(builder.build()) == JOB_ID.get();
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Logger.d("onStartJob");

        final Session session = Session.create();
        session.setName("Auto backup #" + JOB_ID.get());

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {

                DataBackupManager.from(getApplicationContext(), session).performBackupAsync(
                        DataLoaderManager.from(getApplicationContext()).load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build()
                                , category), category, new TransportListenerAdapter() {
                            @Override
                            public void onComplete() {
                                super.onComplete();
                                jobFinished(params, true);
                            }
                        }
                );
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
