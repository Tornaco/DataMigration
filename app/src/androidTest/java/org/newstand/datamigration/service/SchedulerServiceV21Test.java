package org.newstand.datamigration.service;

import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class SchedulerServiceV21Test {
    @Test
    public void schedule() throws Exception {
        SettingsProvider.setBackupInterval(Interval.Minutes);
        Assert.assertTrue(SchedulerServiceV21.schedule(InstrumentationRegistry.getTargetContext()));
        Sleeper.sleepQuietly(Interval.Day.getIntervalMills());
    }

}