package org.newstand.datamigration.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.thread.SharedExecutor;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;

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

    public void loadAsync(@NonNull DataCategory dataCategory,
                          @NonNull LoaderListener<DataRecord> listener) {
        LoaderSource fromResolver = LoaderSource.builder().parent(LoaderSource.Parent.ContentProvider)
                .session(Session.create()).build();
        loadAsync(fromResolver, dataCategory, listener, null);
    }

    public void loadAsync(@NonNull final LoaderSource loaderSource, @NonNull DataCategory dataCategory,
                          @NonNull LoaderListener<DataRecord> listener) {
        loadAsync(loaderSource, dataCategory, listener, null);
    }

    @SuppressWarnings("unchecked")
    public void loadAsync(@NonNull final LoaderSource loaderSource, @NonNull final DataCategory dataCategory,
                          @NonNull final LoaderListener<DataRecord> listener,
                          final LoaderFilter<DataRecord> filter) {
        Logger.d("loadAsync...");
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

    @NonNull
    public DataLoader getLoader(DataCategory type) {
        DataLoader loader = type.getLoader();
        if (loader instanceof ContextWireable) {
            ((ContextWireable) loader).wire(getContext());
        }
        return loader;
    }
}
