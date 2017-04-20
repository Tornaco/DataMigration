package org.newstand.datamigration.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.provider.SettingsProvider;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/4/20 16:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SeLinuxContextChangerTest {
    @Test
    public void restoreContext() throws Exception {
        Assert.assertTrue(SeLinuxContextChanger.restoreContext(SettingsProvider.getAppDataDir()
                + File.separator
                + InstrumentationRegistry.getTargetContext()));
    }

}