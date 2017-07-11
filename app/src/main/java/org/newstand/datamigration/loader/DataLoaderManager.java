package org.newstand.datamigration.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.logger.Logger;

import java.util.Collection;
import java.util.Collections;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 11:17
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataLoaderManager {

    @Getter
    private Context context;

    public synchronized static DataLoaderManager from(@NonNull Context context) {
        return new DataLoaderManager(context);
    }

    public void loadAsync(@NonNull final LoaderSource loaderSource, @NonNull DataCategory dataCategory,
                          @NonNull LoaderListener<DataRecord> listener) {
        loadAsync(loaderSource, dataCategory, listener, null);
    }

    @SuppressWarnings("unchecked")
    public void loadAsync(@NonNull final LoaderSource loaderSource, @NonNull final DataCategory dataCategory,
                          @NonNull final LoaderListener<DataRecord> listener,
                          final LoaderFilter<DataRecord> filter) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onStart();
                    Collection<DataRecord> records = getLoader(dataCategory).load(loaderSource, filter);
                    listener.onComplete(records);
                } catch (Throwable throwable) {
                    listener.onErr(throwable);
                }
            }
        };
        SharedExecutor.execute(r);
    }

    @SuppressWarnings("unchecked")
    public Collection<DataRecord> load(@NonNull final LoaderSource loaderSource,
                                       @NonNull final DataCategory dataCategory) {
        Logger.d("Loading data delegate:%s, for:%s", loaderSource, dataCategory);
        try {
            return (Collection<DataRecord>) getLoader(dataCategory).load(loaderSource, null);
        } catch (Throwable throwable) {
            Logger.e(throwable, "Err when loading");
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataRecord> load(@NonNull final LoaderSource loaderSource,
                                       @NonNull final DataCategory dataCategory,
                                       final LoaderFilter<DataRecord> filter) {
        try {
            return (Collection<DataRecord>) getLoader(dataCategory).load(loaderSource, filter);
        } catch (Throwable throwable) {
            Logger.e(throwable, "Err when loading");
        }
        return Collections.emptyList();
    }

    @NonNull
    public DataLoader getLoader(DataCategory type) {
        DataLoader loader = type.getLoader();
        if (loader instanceof ContextWireable) {
            ((ContextWireable) loader).wire(getContext());
        }
        return loader;
    }
}
