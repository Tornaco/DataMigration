package org.newstand.datamigration.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Nick@NewStand.org on 2017/4/26 10:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class WifiUtilsTest {
    @Test
    public void setWifiEnabled() throws Exception {
        WifiUtils.setWifiEnabled(InstrumentationRegistry.getTargetContext(), true);
        WifiUtils.setWifiEnabled(InstrumentationRegistry.getTargetContext(), false);
        WifiUtils.setWifiEnabled(InstrumentationRegistry.getTargetContext(), true);
    }

}