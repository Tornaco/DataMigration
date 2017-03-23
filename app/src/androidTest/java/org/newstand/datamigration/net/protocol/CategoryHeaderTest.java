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
import org.newstand.datamigration.data.model.DataRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick@NewStand.org on 2017/3/22 9:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class CategoryHeaderTest {

    @Test
    public void testCategoryHeader() throws ExecutionException {
        Context appContext = InstrumentationRegistry.getTargetContext();

        LoadingCacheManager.createDroid(appContext);

        Collection<DataRecord> dataRecordCollections = LoadingCacheManager.droid().get(DataCategory.App);

        CategoryHeader header = CategoryHeader.from(DataCategory.App);
        header.add(dataRecordCollections);

        byte[] data = header.toBytes();

        Logger.d(Arrays.toString(data));

        Logger.d(header);

        CategoryHeader h2 = CategoryHeader.from(data);

        Logger.d(h2);

        Assert.assertTrue(h2.equals(header));

    }
}