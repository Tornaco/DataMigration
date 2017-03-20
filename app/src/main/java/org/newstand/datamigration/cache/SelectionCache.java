package org.newstand.datamigration.cache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.newstand.datamigration.loader.LoaderSource.Parent.Android;

/**
 * Created by Nick@NewStand.org on 2017/3/15 19:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SelectionCache {

    private static SelectionCache sMe;

    private Map<Session, LoadingCache<DataCategory, Collection<DataRecord>>> mSessionMap;

    public static synchronized SelectionCache from(@NonNull Context context) {
        if (sMe == null) sMe = new SelectionCache();
        return sMe;
    }

    private LoadingCache<DataCategory, Collection<DataRecord>> android;

    private SelectionCache() {
        mSessionMap = new HashMap<>();
    }

    public void cleanUp() {
        if (android != null) android.cleanUp();

        for (LoadingCache<DataCategory, Collection<DataRecord>> c : mSessionMap.values()) {
            if (c != null)
                c.cleanUp();
        }
    }

    public LoadingCache<DataCategory, Collection<DataRecord>> fromSource(LoaderSource source, Context context) {
        switch (source.getParent()) {
            case Android:
                return getAndroid(context);
            case Backup:
                return getBackup(source.getSession(), context);
            default:
                return null;
        }
    }

    protected synchronized LoadingCache<DataCategory, Collection<DataRecord>> getBackup(final Session session, final Context context) {
        if (mSessionMap.get(session) == null) {
            LoadingCache<DataCategory, Collection<DataRecord>> o = CacheBuilder.newBuilder().maximumSize(DataCategory.values().length)
                    .expireAfterAccess(365, TimeUnit.DAYS)
                    .build(new CacheLoader<DataCategory, Collection<DataRecord>>() {
                        @Override
                        public Collection<DataRecord> load(@NonNull DataCategory key) throws Exception {
                            DataLoaderManager manager = DataLoaderManager.from(context);
                            return manager.load(LoaderSource.builder().parent(LoaderSource.Parent.Backup)
                                    .session(session).build(), key);
                        }
                    });
            mSessionMap.put(session, o);
            return o;
        }
        return mSessionMap.get(session);
    }

    protected synchronized LoadingCache<DataCategory, Collection<DataRecord>> getAndroid(final Context context) {
        if (android == null)
            android = CacheBuilder.newBuilder().maximumSize(DataCategory.values().length)
                    .expireAfterAccess(365, TimeUnit.DAYS)
                    .build(new CacheLoader<DataCategory, Collection<DataRecord>>() {
                        @Override
                        public Collection<DataRecord> load(@NonNull DataCategory key) throws Exception {
                            DataLoaderManager manager = DataLoaderManager.from(context);
                            return manager.load(LoaderSource.builder().parent(Android)
                                    .session(Session.create()).build(), key);
                        }
                    });
        return android;
    }
}
