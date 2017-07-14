package org.newstand.datamigration.loader.impl;

import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;

import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.CallLogRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/4/19 13:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CallLogLoader extends BaseLoader {
    @Override
    public String[] needPermissions() {
        return new String[0];
    }

    @Override
    public Collection<DataRecord> loadFromAndroid(final LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        String[] projection = new String[]{CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE,
                CallLog.Calls.DATE};
        consumeCursor(createCursor(CallLog.Calls.CONTENT_URI, projection, null,
                null, CallLog.Calls.DEFAULT_SORT_ORDER), new Consumer<Cursor>() {
            @Override
            public void accept(@NonNull Cursor cursor) {
                DataRecord record = recordFromCursor(cursor);
                if (record != null && (filter == null || !filter.ignored(record)))
                    records.add(record);
            }
        });
        return records;
    }

    private DataRecord recordFromCursor(Cursor cursor) {
        CallLogRecord callLogRecord = new CallLogRecord();
        callLogRecord.setNum(cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
        callLogRecord.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
        callLogRecord.setName(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
        callLogRecord.setType(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));
        callLogRecord.setDisplayName(callLogRecord.getNum());
        try {
            callLogRecord.setSize(callLogRecord.calculateSize());
        } catch (IOException e) {
            Logger.e(e, "Fail query size");
        }
        return callLogRecord;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.CallLog, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.CallLog, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                try {
                    String fileToParse = file.getPath();
                    boolean isEncrypted = SettingsProvider.isEncryptedFile(file.getPath());
                    Logger.i("isEncrypted %s %s", file, isEncrypted);
                    String fileToDecrypt = isEncrypted ?
                            SettingsProvider.getDecryptPath(fileToParse) : null;
                    if (isEncrypted && EncryptManager.from(getContext()).decrypt(fileToParse, fileToDecrypt)) {
                        Logger.i("Change file to parse %s", fileToDecrypt);
                        fileToParse = fileToDecrypt;
                    }
                    String content = org.newstand.datamigration.utils.Files.readString(fileToParse);
                    Gson gson = new Gson();
                    CallLogRecord record = gson.fromJson(content, CallLogRecord.class);
                    record.setPath(file.getPath());
                    record.setChecked(false);
                    record.setSize(Files.asByteSource(file).size());
                    records.add(record);
                    // Delete decrypted file
                    if (fileToDecrypt != null) {
                        File fileToDelete = new File(fileToDecrypt);
                        if (fileToDelete.exists()) BlackHole.eat(fileToDelete.delete());
                    }
                } catch (Throwable t) {
                    Logger.e(t, "Fail to parse call log");
                }
            }
        });

        return records;
    }
}
