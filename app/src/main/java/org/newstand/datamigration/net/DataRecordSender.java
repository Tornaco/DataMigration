package org.newstand.datamigration.net;

import android.text.TextUtils;

import com.google.common.collect.Lists;

import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.net.protocol.FileHeader;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataRecordSender extends AbsSender<DataRecord> {

    private OutputStream os;
    private InputStream is;
    private Session session;

    private DataRecordSender(OutputStream os, InputStream is, Session session) {
        this.os = os;
        this.is = is;
        this.session = session;
    }

    public static DataRecordSender with(OutputStream os, InputStream is, Session session) {
        return new DataRecordSender(os, is, session);
    }

    @Override
    public int send(DataRecord dataRecord) throws IOException {

        FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;

        String path = fileBasedRecord.getPath();

        // Check if we need to export to tmp first.
        if (path == null) {
            DataBackupManager.from(getContext(), session).performBackup(Lists.newArrayList(dataRecord),
                    dataRecord.category());
            path = fileBasedRecord.getPath();
        }

        Logger.d("DataRecordSender, path=%s", path);

        String name = null;
        switch (dataRecord.category()) {
            default:
                name = new File(path).getName().replace(" ", "");// Remove space.
                break;
        }

        FileHeader fileHeader = FileHeader.from(fileBasedRecord.getPath(), fixedName(name));

        Logger.d("Sending: %s", fileHeader.toString());

        fileHeader.writeTo(os);

        int ret = waitForAck(is);

        if (ret != OK) return ret;

        writeFile(path, os);

        ret = waitForAck(is);

        return ret;
    }

    private String fixedName(String from) {
        if (TextUtils.isEmpty(from)) {
            return UUID.randomUUID().toString();
        }
        return from;
    }
}
