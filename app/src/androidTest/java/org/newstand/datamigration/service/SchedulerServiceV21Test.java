package org.newstand.datamigration.service;

import android.support.test.InstrumentationRegistry;

import com.google.common.collect.ImmutableList;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.service.schedule.BackupActionSettings;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.ScheduleAction;
import org.newstand.datamigration.service.schedule.ScheduleActionType;
import org.newstand.datamigration.service.schedule.SchedulerServiceV21;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.worker.transport.Session;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class SchedulerServiceV21Test {
    @Test
    public void schedule() throws Exception {
        SettingsProvider.setBackupInterval(Interval.Minutes);
        Assert.assertTrue(SchedulerServiceV21.schedule(InstrumentationRegistry.getTargetContext(),
                Condition.builder()
                        .isPersisted(true)
                        .requiresDeviceIdle(false)
                        .requiresCharging(true)
                        .build(),
                ScheduleAction.builder()
                        .actionType(ScheduleActionType.Backup)
                        .settings(BackupActionSettings.builder()
                                .session(Session.from("SchedulerService"))
                                .dataCategories(ImmutableList.of(
                                        DataCategory.CallLog,
                                        DataCategory.Contact))
                                .build())
                        .build()));
        Sleeper.sleepQuietly(Interval.Day.getIntervalMills());
    }

}