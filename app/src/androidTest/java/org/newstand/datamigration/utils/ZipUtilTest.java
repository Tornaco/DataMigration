package org.newstand.datamigration.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 * Created by Nick@NewStand.org on 2017/4/7 11:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ZipUtilTest {

    @Test
    public void compress() throws IOException {

        // Pack
        Session session = Session.create();

        DataBackupManager.from(InstrumentationRegistry.getTargetContext(), session)
                .performBackup(DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                        .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.Photo), DataCategory.Photo);


        final String zipPath = SettingsProvider.getTestDir() + File.separator + "test_pack.zip";
        com.google.common.io.Files.createParentDirs(new File(zipPath));
        ZipUtil.pack(new File(SettingsProvider.getBackupSessionDir(session)), new File(zipPath));

        Sleeper.sleepQuietly(10 * 1000);

        // List
        Assert.assertTrue(ZipUtil.containsEntry(new File(zipPath), SettingsProvider.getBackupSessionInfoFileName()));

        ZipUtil.iterate(new File(zipPath), new ZipEntryCallback() {
            @Override
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                Logger.v(zipEntry.toString());
            }
        });
    }
}
