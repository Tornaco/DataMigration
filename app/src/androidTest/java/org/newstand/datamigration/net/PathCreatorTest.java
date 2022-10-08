package org.newstand.datamigration.net;

import android.content.Context;
import androidx.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/27 14:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class PathCreatorTest {
    @Test
    public void createIfNull() throws Exception {
        final Context context = InstrumentationRegistry.getTargetContext();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecordCollection = DataLoaderManager.from(context)
                        .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), category);

                Session session = Session.tmp();

                PathCreator.createIfNull(context, session, dataRecordCollection);

                Collections.consumeRemaining(dataRecordCollection, new Consumer<DataRecord>() {
                    @Override
                    public void accept(@NonNull DataRecord dataRecord) {
                        FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;

                        Assert.assertTrue(fileBasedRecord.getPath() != null);

                        Logger.d(fileBasedRecord);
                    }
                });
            }
        });
    }

}