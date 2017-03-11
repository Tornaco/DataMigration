package org.newstand.datamigration.loader.impl;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.model.MusicRecord;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class MusicLoader extends BaseLoader {

    static String sArtworkUri = "content://media/external/audio/albumart";

    @Override
    public Collection<DataRecord> loadFromContentProvider(final LoaderFilter<DataRecord> filter) {

        final Collection<DataRecord> records = new ArrayList<>();

        consumeCursor(createCursor(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER), new Consumer<Cursor>() {
            @Override
            public void consume(@NonNull Cursor cursor) {
                DataRecord record = recordFromCursor(cursor);
                if (record != null && (filter == null || !filter.ignored(record)))
                    records.add(record);
            }
        });
        return records;
    }

    @Override
    public Collection<DataRecord> loadFromBackup(Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        String dir = SettingsProvider.getBackupDirByCategory(DataCategory.Music, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void consume(@NonNull File file) {
                MusicRecord record = new MusicRecord();
                record.setDisplayName(file.getName());
                record.setPath(file.getAbsolutePath());
                records.add(record);
            }
        });

        return records;
    }


    private DataRecord recordFromCursor(Cursor cursor) {

        boolean isMusic = cursor.getInt(cursor
                .getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC)) != 0;

        if (!isMusic) return null;

        long id = cursor
                .getLong(cursor.getColumnIndex(BaseColumns._ID));

        String singer = cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));

        int duration = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

        String name = cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));

        long albumid = cursor.getLong(cursor
                .getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));

        String url = cursor.getString(cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

        MusicRecord record = new MusicRecord();
        record.setDisplayName(name);
        record.setArtist(singer);
        record.setPath(url);
        record.setId(String.valueOf(id));
        record.setDuration(duration);
        record.setArtUri(sArtworkUri + File.separator + albumid);
        return record;
    }

    @Override
    public String[] needPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        return null;
    }
}
