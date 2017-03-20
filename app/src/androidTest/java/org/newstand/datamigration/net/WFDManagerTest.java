package org.newstand.datamigration.net;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.net.wfd.WFDManager;
import org.newstand.datamigration.sync.Sleeper;

/**
 * Created by Nick@NewStand.org on 2017/3/14 10:54
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class WFDManagerTest {

    @Test
    public void testStart() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        WFDManager service = WFDManager.builder(appContext).build();
        service.start();

        service.setDeviceName("Test_set_name");

        Sleeper.sleepQuietly(60 * 10 * 1000);
    }
}