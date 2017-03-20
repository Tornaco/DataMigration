package org.newstand.datamigration.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.utils.Collections;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick@NewStand.org on 2017/3/15 19:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SelectionCacheTest {

    @Test
    public void testGet() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        SelectionCache cache = SelectionCache.from(appContext);

        try {
            Collections.consumeRemaining(cache.getAndroid().get(DataCategory.App), new Consumer<DataRecord>() {
                @Override
                public void consume(@NonNull DataRecord dataRecord) {
                    Logger.d(dataRecord);
                    dataRecord.setChecked(true);
                    dataRecord.setDisplayName("@@@" + dataRecord.getDisplayName());
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet2() {
        testGet();
        Logger.d("-------------------");
        testGet();
        Logger.d("-------------------");
        testGet();
    }


    @Test
    public void testGet3() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        SelectionCache cache = SelectionCache.from(appContext);


        try {
            Collection<DataRecord> dataRecords = cache.getAndroid().get(DataCategory.App);

            Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
                @Override
                public void consume(@NonNull DataRecord dataRecord) {
                    Logger.d(dataRecord);
                    dataRecord.setChecked(true);
                    dataRecord.setDisplayName("@@@" + dataRecord.getDisplayName());
                }
            });

            cache.getAndroid().put(DataCategory.App, dataRecords);

            testGet();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

}