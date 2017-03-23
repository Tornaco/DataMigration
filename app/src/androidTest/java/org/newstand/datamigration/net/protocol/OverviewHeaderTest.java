package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.model.DataCategory;

import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/3/21 14:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

@RunWith(AndroidJUnit4.class)
public class OverviewHeaderTest {

    @Test
    public void testAll() {

        Context appContext = InstrumentationRegistry.getTargetContext();

        LoadingCacheManager.createDroid(appContext);

        OverviewHeader header = OverviewHeader.empty();

        header.add(DataCategory.App, LoadingCacheManager.droid().get(DataCategory.App));

        Logger.e(header.toString());

        byte[] data = header.toBytes();

        Logger.e(Arrays.toString(data));

        OverviewHeader h2 = OverviewHeader.empty();
        h2.inflateWithBytes(data);

        Assert.assertTrue(h2.getFileCount() > 0 && h2.getFileSize() > 0);
        Assert.assertTrue(h2.getFileSize() == header.getFileSize());

        Logger.e(h2.toString());

    }
}