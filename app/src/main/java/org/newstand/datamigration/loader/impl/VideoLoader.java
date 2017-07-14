package org.newstand.datamigration.loader.impl;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.VideoRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/7 14:39
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VideoLoader extends BaseLoader {
    @Override
    public Collection<DataRecord> loadFromAndroid(final LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        consumeCursor(createCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Video.Media.DATE_MODIFIED + " desc"), new Consumer<Cursor>() {
            @Override
            public void accept(@NonNull Cursor cursor) {
                DataRecord record = recordFromCursor(cursor);
                if (record != null && (filter == null || !filter.ignored(record)))
                    records.add(record);
            }
        });
        return records;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.Video, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.Video, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                VideoRecord record = new VideoRecord();
                record.setDisplayName(file.getName());
                record.setPath(file.getAbsolutePath());
                try {
                    record.setSize(record.calculateSize());
                } catch (IOException e) {
                    Logger.e("Fail calculate size");
                }
                records.add(record);
            }
        });

        return records;

    }

    private DataRecord recordFromCursor(Cursor cursor) {

        VideoRecord record = new VideoRecord();

        int id = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
        String title = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
        String album = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
        String artist = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
        String displayName = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
        String mimeType = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
        String path = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
        long duration = cursor
                .getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
        long size = cursor
                .getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

        record.setId(String.valueOf(id));
        record.setPath(path);
        record.setDisplayName(new File(path).getName()); // Fix file name.
        record.setSize(size);
        record.setChecked(false);
        record.setDuration(duration);

        return record;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
