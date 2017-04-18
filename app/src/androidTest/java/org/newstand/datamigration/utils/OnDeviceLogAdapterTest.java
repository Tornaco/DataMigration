package org.newstand.datamigration.utils;

import org.junit.Before;
import org.junit.Test;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

/**
 * Created by Nick@NewStand.org on 2017/4/1 10:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class OnDeviceLogAdapterTest {
    @Before
    public void setUp() throws Exception {
        Logger.config(Settings
                .builder()
                .logLevel(Logger.LogLevel.ALL)
                .tag("OnDeviceLogAdapterTest")
                .logAdapter(new OnDeviceLogAdapter())
                .build());
    }

    @Test
    public void d() throws Exception { //Should disable realm out write.
        for (int i =0; i < 10 * 1000; i++) {
            Logger.d("");
        }
        Sleeper.sleepQuietly(10 * 1000);
    }

}