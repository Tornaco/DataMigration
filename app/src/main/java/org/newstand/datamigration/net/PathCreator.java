package org.newstand.datamigration.net;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/27 14:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class PathCreator {

    public static void createIfNull(Context context, Session session, Collection<DataRecord> dataRecords) {
        if (Collections.isNullOrEmpty(dataRecords)) return;

        final List<DataRecord> workingList = new ArrayList<>();

        Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord dataRecord) {
                FileBasedRecord fileBasedRecord = (FileBasedRecord) dataRecord;
                String path = fileBasedRecord.getPath();
                if (path == null) workingList.add(dataRecord);
            }
        });

        if (Collections.isNullOrEmpty(workingList)) return;

        Logger.d("Creating path with session %s, file count %d", session, workingList.size());

        DataBackupManager.from(context, session).performBackup(workingList, workingList.get(0).category());
    }
}
