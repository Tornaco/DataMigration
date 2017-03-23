package org.newstand.datamigration.net.protocol;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderListener;
import org.newstand.datamigration.loader.LoaderSource;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/22 12:55
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class FileHeaderTest {

    @Test
    public void testFileHeader() {

        DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .loadAsync(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App, new LoaderListener<DataRecord>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(Collection<DataRecord> collection) {
                        DataRecord record = collection.iterator().next();

                        FileBasedRecord fileBasedRecord = (FileBasedRecord) record;

                        FileHeader header = null;
                        try {
                            header = FileHeader.from(fileBasedRecord.getPath());
                        } catch (IOException e) {
                            Assert.fail(e.getLocalizedMessage());
                        }
                        FileHeader h2 = FileHeader.from(header.toBytes());
                        Assert.assertTrue(header.equals(h2));

                        Logger.d(h2.toString());
                    }

                    @Override
                    public void onErr(Throwable throwable) {
                        Assert.fail(throwable.getLocalizedMessage());
                    }
                });


    }
}