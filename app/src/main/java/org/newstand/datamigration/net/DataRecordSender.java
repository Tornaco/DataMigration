package org.newstand.datamigration.net;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.net.protocol.FileHeader;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

        FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;

        String path = fileBasedRecord.getPath();

        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(path);

        FileHeader fileHeader = FileHeader.from(fileBasedRecord.getPath(), fileBasedRecord.getDisplayName());

        Logger.d("Sending: %s", fileHeader.toString());

        fileHeader.writeTo(os);

        int ret = waitForAck(is);

        if (ret != OK) return ret;

        writeFile(path, os);

        ret = waitForAck(is);

        return ret;
    }
}
