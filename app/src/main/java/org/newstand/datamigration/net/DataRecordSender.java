package org.newstand.datamigration.net;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.FileHeader;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.worker.backup.DataBackupManager;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataRecordSender extends AbsSender<DataRecord> {

    private OutputStream os;
    private InputStream is;

    private DataRecordSender(OutputStream os, InputStream is) {
        this.os = os;
        this.is = is;
    }

    public static DataRecordSender with(OutputStream os, InputStream is) {
        return new DataRecordSender(os, is);
    }

    @Override
    public int send(DataRecord dataRecord) throws IOException {

        String tmp = null;

        FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;

        String path = fileBasedRecord.getPath();

        if (path == null) {
            tmp = createTmpPath(fileBasedRecord);
            path = fileBasedRecord.getPath();
        }

        FileHeader fileHeader = FileHeader.from(fileBasedRecord.getPath());

        Logger.d("Sending: %s", fileHeader.toString());

        fileHeader.writeTo(os);

        int ret = waitForAck(is);

        if (ret != OK) return ret;

        writeFile(path, os);

        ret = waitForAck(is);

        if (tmp != null) {
            BlackHole.eat(new File(tmp).getParentFile().delete());
        }

        return ret;
    }

    private String createTmpPath(FileBasedRecord dataRecord) {

        Session session = Session.random();

        DataBackupManager backupManager = DataBackupManager.from(getContext(), session);

        DataLoaderManager loaderManager = DataLoaderManager.from(getContext());

        switch (dataRecord.category()) {
            case Contact:
            case Sms:
                Collection<DataRecord> r = new ArrayList<>(1);
                r.add(dataRecord);
                backupManager.performBackup(r, dataRecord.category());

                Collection<DataRecord> output = loaderManager.load(LoaderSource.builder().parent(LoaderSource.Parent.Backup)
                        .session(session).build(), dataRecord.category(), new LoaderFilter<DataRecord>() {
                    @Override
                    public boolean ignored(@NonNull DataRecord ths) {
                        return false;
                    }
                });

                int size = output.size();

                if (size != 1) {
                    Logger.w("Bad size: %d", size);
                }

                // FIXME Check count
                FileBasedRecord nr = (FileBasedRecord) output.toArray()[0];

                Logger.d("Updated nr:" + nr);

                dataRecord.setPath(nr.getPath());

                return nr.getPath();

            default:
                throw new IllegalStateException("Should not come here for " + dataRecord);
        }


    }

}
