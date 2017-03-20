package org.newstand.datamigration.loader.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.common.PermissionRelyed;
import org.newstand.datamigration.loader.DataLoader;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class BaseLoader implements DataLoader<DataRecord>, PermissionRelyed, ContextWireable {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Context context;

    public abstract Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter);

    public abstract Collection<DataRecord> loadFromBackup(Session session, LoaderFilter<DataRecord> filter);

    @Override
    public final Collection<DataRecord> load(LoaderSource source, LoaderFilter<DataRecord> filter) {
        switch (source.getParent()) {
            case Backup:
                return loadFromBackup(source.getSession(), filter);
            case Android:
                return loadFromAndroid(filter);
        }
        return null;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    protected Cursor createCursor(@NonNull Uri uri,
                                  @Nullable String[] projection, @Nullable String selection,
                                  @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        ContentResolver cr = getContext().getContentResolver();
        return cr.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    protected void consumeCursor(Cursor cursor, Consumer<Cursor> cursorConsumer) {
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                cursorConsumer.consume(cursor);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    protected <T> List<T> newList() {
        return new ArrayList<>();
    }
}
