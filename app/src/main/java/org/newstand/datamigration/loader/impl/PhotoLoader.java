package org.newstand.datamigration.loader.impl;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.PhotoRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/7 14:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class PhotoLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(final LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = newList();
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] selectionArgs = {"image/jpeg"};
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        consumeCursor(createCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder),
                new Consumer<Cursor>() {
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
                SettingsProvider.getReceivedDirByCategory(DataCategory.Photo, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.Photo, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                PhotoRecord record = new PhotoRecord();
                record.setDisplayName(file.getName());
                record.setPath(file.getAbsolutePath());
                try {
                    record.setSize(Files.asByteSource(file).size());
                } catch (IOException ignored) {

                }
                records.add(record);
            }
        });

        return records;
    }

    private DataRecord recordFromCursor(Cursor cursor) {
        PhotoRecord r = new PhotoRecord();
        r.setId(cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media._ID)));
        r.setDisplayName(cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
        r.setSize(cursor.getLong(cursor
                .getColumnIndex(MediaStore.Images.Media.SIZE)));
        r.setPath(cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA)));
        return r;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
