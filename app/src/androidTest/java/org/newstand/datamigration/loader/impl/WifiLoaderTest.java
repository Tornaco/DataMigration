package org.newstand.datamigration.loader.impl;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

/**
 * Created by Nick@NewStand.org on 2017/4/25 16:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class WifiLoaderTest {
    @Test
    public void loadFromAndroid() throws Exception {
        WifiLoader loader = new WifiLoader();
        loader.wire(InstrumentationRegistry.getTargetContext());
        loader.loadFromAndroid(null);
    }

}