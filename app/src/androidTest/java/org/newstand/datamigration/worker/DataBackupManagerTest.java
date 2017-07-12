package org.newstand.datamigration.worker;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderListener;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListenerAdapter;
import org.newstand.datamigration.worker.transport.backup.ContactBackupAgent;
import org.newstand.datamigration.worker.transport.backup.ContactRestoreSettings;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nick@NewStand.org on 2017/3/9 9:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class DataBackupManagerTest {

    private Session session = Session.create();

    @Test
    public void testBackup() {

        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

        final CountDownLatch latch = new CountDownLatch(2);

        LoaderSource fromResolver = LoaderSource.builder().parent(LoaderSource.Parent.Android)
                .session(Session.create()).build();

        DataLoaderManager.from(appContext).loadAsync(
                fromResolver,
                DataCategory.Contact, new LoaderListener<DataRecord>() {
                    @Override
                    public void onStart() {
                        Logger.d("onStart");
                    }

                    @Override
                    public void onComplete(Collection<DataRecord> collection) {
                        latch.countDown();
                        DataBackupManager.from(appContext).performBackupAsync(collection, DataCategory.Contact,
                                new TransportListenerAdapter() {
                                    @Override
                                    public void onStart() {
                                        Logger.d("onStart");
                                    }

                                    @Override
                                    public void onRecordStart(DataRecord record) {
                                        Logger.d("onRecordStart:" + record);
                                    }

                                    @Override
                                    public void onRecordSuccess(DataRecord record) {
                                        Logger.d("onRecordSuccess:" + record);
                                    }

                                    @Override
                                    public void onRecordFail(DataRecord record, Throwable err) {
                                        Logger.d("onRecordFail:" + record + err);
                                    }

                                    @Override
                                    public void onProgressUpdate(float progress) {
                                        super.onProgressUpdate(progress);
                                        Logger.d("onProgressUpdate:" + progress);
                                    }

                                    @Override
                                    public void onRecordProgressUpdate(DataRecord record, RecordEvent recordEvent, float progress) {
                                        super.onRecordProgressUpdate(record, recordEvent, progress);
                                        Logger.d("onRecordProgressUpdate:%s, %s", recordEvent, progress);
                                    }

                                    @Override
                                    public void onComplete() {
                                        Logger.d("onComplete");

                                        ContactRestoreSettings settings = new ContactRestoreSettings();
                                        settings.setSourcePath(SettingsProvider.getBackupDirByCategory(DataCategory.Contact, session)
                                                + "/Nick@7.vcf");
                                        try {
                                            ContactBackupAgent agent = new ContactBackupAgent();
                                            agent.setContext(appContext);
                                            agent.restore(settings);
                                        } catch (Exception e) {
                                            Logger.e(Log.getStackTraceString(e));
                                            Assert.fail();
                                        }

                                        // TEST LOAD FROM BACKUP
                                        LoaderSource loaderSource = LoaderSource.builder()
                                                .parent(LoaderSource.Parent.Backup)
                                                .session(session).build();
                                        DataLoaderManager.from(appContext)
                                                .loadAsync(loaderSource, DataCategory.Contact, new LoaderListener<DataRecord>() {
                                                    @Override
                                                    public void onStart() {
                                                        Logger.d("Load delegate backup.prepareForTransporting");
                                                    }

                                                    @Override
                                                    public void onComplete(Collection<DataRecord> collection) {
                                                        Logger.d("Load delegate backup.onComplete:%s", collection);
                                                        latch.countDown();
                                                    }

                                                    @Override
                                                    public void onErr(Throwable throwable) {
                                                        Logger.d("Load delegate backup.err:" + Log.getStackTraceString(throwable));
                                                    }
                                                });

                                    }
                                });
                    }

                    @Override
                    public void onErr(Throwable throwable) {
                        Assert.fail();
                    }
                });

        try {
            latch.await(300 * 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }
}