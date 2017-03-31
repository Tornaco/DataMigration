package org.newstand.datamigration.provider;

import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/3/31 15:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsProviderTest {
    @Test
    public void getTransportServerPorts() throws Exception {
        int[] ports = SettingsProvider.getTransportServerPorts();
        Logger.d(Arrays.toString(ports));
        Assert.assertTrue(ports.length > 0);
    }

}