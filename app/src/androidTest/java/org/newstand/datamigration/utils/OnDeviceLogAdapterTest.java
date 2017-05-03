package org.newstand.datamigration.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;
import org.newstand.logger.Settings;

import java.io.File;
import java.io.PrintStream;
import java.util.UUID;

/**
 * Created by Nick@NewStand.org on 2017/4/1 10:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
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
        for (int i = 0; i < 1000; i++) {
            Logger.d("Hello world! " + i);
        }
        Sleeper.sleepQuietly(10 * 1000);
    }

    @Test
    public void startRedirection() throws Exception {

        config();

        File tar = new File("/sdcard/printer.test" + UUID.randomUUID());

        Assert.assertTrue(tar.createNewFile());

        PrintStream stream = new PrintStream(tar);

        Logger.startRedirection(stream);

        d();

        stopRedirection();

        d();

        Logger.v("X" + Files.readString(tar.getPath()));
    }

    @Test
    public void stopRedirection() throws Exception {
        Logger.stopRedirection();
    }

    @Test
    public void config() throws Exception {
        Logger.config(Settings.builder()
                .logLevel(Logger.LogLevel.VERBOSE)
                .tag("DataM")
                .build());
    }
}