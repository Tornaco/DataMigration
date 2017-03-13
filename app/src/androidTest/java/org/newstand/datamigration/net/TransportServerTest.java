package org.newstand.datamigration.net;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Nick@NewStand.org on 2017/3/13 18:11
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class TransportServerTest {

    @Test
    public void testNetty() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        new TransportServer(appContext).startServer();
    }
}