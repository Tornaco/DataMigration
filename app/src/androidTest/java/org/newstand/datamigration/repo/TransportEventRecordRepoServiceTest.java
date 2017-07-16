package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

/**
 * Created by guohao4 on 2017/7/11.
 */
public class TransportEventRecordRepoServiceTest {

    Session session = Session.create();
    TransportType transportType = TransportType.Backup;

    @Test
    public void testInsert() {
        new TransportEventRecordRepoService(session, transportType).drop();

        Collections.consumeRemaining(DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .getLoader(DataCategory.Sms)
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), null), new Consumer() {
            @Override
            public void accept(@NonNull Object o) {
                DataRecord record = (DataRecord) o;
                TransportEventRecord transportEventRecord = TransportEventRecord.builder()
                        .dataRecord(record)
                        .errMessage("ERR")
                        .errTrace(Logger.getStackTraceString(new IllegalStateException()))
                        .success(true)
                        .when(System.currentTimeMillis())
                        .build();

                Assert.assertTrue(new TransportEventRecordRepoService(session, transportType).insert(InstrumentationRegistry.getTargetContext(), transportEventRecord));
            }
        });


        Collections.consumeRemaining(new TransportEventRecordRepoService(session, transportType).
                findAll(InstrumentationRegistry.getTargetContext()), new Consumer<TransportEventRecord>() {
            @Override
            public void accept(@NonNull TransportEventRecord transportEventRecord) {
                Logger.d(transportEventRecord.toString());
            }
        });
    }

}