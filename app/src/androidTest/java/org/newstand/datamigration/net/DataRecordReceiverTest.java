package org.newstand.datamigration.net;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.FileHeader;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class DataRecordReceiverTest {


    @Test
    public void testIO() throws IOException {
        File srcFile = createFile();
        File toFile = new File(InstrumentationRegistry.getTargetContext().getFilesDir().getAbsoluteFile() + File.separator + srcFile.getName());

        Logger.d("to: %s", toFile.getPath());

        FileHeader fileHeader = FileHeader.from(srcFile.getPath(), srcFile.getName());

        Logger.d(fileHeader);
    }

    private File createFile() {
        DataRecord r = DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App)
                .iterator().next();
        return new File(((FileBasedRecord) r).getPath());
    }

}