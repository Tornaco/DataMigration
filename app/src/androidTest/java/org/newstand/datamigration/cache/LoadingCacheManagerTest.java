package org.newstand.datamigration.cache;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/3/23 10:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class LoadingCacheManagerTest {

    @Test
    public void testDroid() {
        LoadingCacheManager.createDroid(InstrumentationRegistry.getTargetContext());
        LoadingCacheManager cache = LoadingCacheManager.droid();

        Collections.consumeRemaining(cache.get(DataCategory.App), new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord dataRecord) {
                Logger.d(dataRecord);
            }
        });
    }

    @Test
    public void testBK() {
        LoadingCacheManager.createBK(InstrumentationRegistry.getTargetContext(), Session.create());
        LoadingCacheManager cache = LoadingCacheManager.bk();
        Assert.assertTrue(cache.get(DataCategory.App) != null);
    }
}