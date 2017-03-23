package org.newstand.datamigration.cache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.newstand.datamigration.loader.LoaderSource.Parent.Android;

/**
 * Created by Nick@NewStand.org on 2017/3/23 10:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class LoadingCacheManager {

    private static LoadingCacheManager droid, bk;

    public static LoadingCacheManager droid() {
        return droid;
    }

    public static LoadingCacheManager bk() {
        return bk;
    }

    public static void createDroid(Context appContext) {
        Droid.create(appContext);
        droid = Droid.sMe;
    }

    public static void createBK(Context appContext, Session session) {
        BK.create(appContext, session);
        bk = BK.sMe;
    }

    public abstract
    @NonNull
    Collection<DataRecord> get(DataCategory key);

    public abstract
    @NonNull
    Collection<DataRecord> getRefreshed(DataCategory key);

    public abstract void refresh();

    public abstract void clear();

    private static class Droid extends LoadingCacheManager {

        private LoadingCache<DataCategory, Collection<DataRecord>> cache;

        private static LoadingCacheManager sMe;

        private Droid(final Context c) {
            cache = CacheBuilder.newBuilder().maximumSize(DataCategory.values().length)
                    .expireAfterAccess(365, TimeUnit.DAYS)
                    .build(new CacheLoader<DataCategory, Collection<DataRecord>>() {
                        @Override
                        public Collection<DataRecord> load(@NonNull DataCategory key) throws Exception {
                            DataLoaderManager manager = DataLoaderManager.from(c);
                            return manager.load(LoaderSource.builder().parent(Android)
                                    .session(Session.create()).build(), key);
                        }
                    });
        }

        public static void create(Context appContext) {
            if (sMe != null) sMe.clear();
            sMe = new Droid(appContext);
        }

        @NonNull
        @Override
        public Collection<DataRecord> get(DataCategory key) {
            try {
                return cache.get(key);
            } catch (ExecutionException e) {
                Logger.e("Fail to get from cache: %s", android.util.Log.getStackTraceString(e));
                return java.util.Collections.emptyList();
            }
        }

        @NonNull
        @Override
        public Collection<DataRecord> getRefreshed(DataCategory key) {
            cache.refresh(key);
            return get(key);
        }

        @Override
        public void refresh() {

        }

        @Override
        public void clear() {
            Logger.d("clearing DROID");
            cache.cleanUp();
            cache.invalidateAll();
        }
    }

    public static class BK extends LoadingCacheManager {

        private LoadingCache<DataCategory, Collection<DataRecord>> cache;

        private static LoadingCacheManager sMe;

        public BK(final Context context, final Session session) {
            cache = CacheBuilder.newBuilder().maximumSize(DataCategory.values().length)
                    .expireAfterAccess(365, TimeUnit.DAYS)
                    .build(new CacheLoader<DataCategory, Collection<DataRecord>>() {
                        @Override
                        public Collection<DataRecord> load(@NonNull DataCategory key) throws Exception {
                            DataLoaderManager manager = DataLoaderManager.from(context);
                            return manager.load(LoaderSource.builder().parent(LoaderSource.Parent.Backup)
                                    .session(session).build(), key);
                        }
                    });
        }

        public static void create(Context context, Session session) {
            if (sMe != null) sMe.clear();
            Preconditions.checkNotNull(session);
            sMe = new BK(context, session);
        }

        @NonNull
        @Override
        public Collection<DataRecord> get(DataCategory key) {
            try {
                return cache.get(key);
            } catch (ExecutionException e) {
                Logger.e("Fail to get from cache: %s", android.util.Log.getStackTraceString(e));
                return java.util.Collections.emptyList();
            } catch (CacheLoader.InvalidCacheLoadException e) {
                Logger.e("Fail to get from cache: %s", android.util.Log.getStackTraceString(e));
                return java.util.Collections.emptyList();
            }
        }

        @NonNull
        @Override
        public Collection<DataRecord> getRefreshed(DataCategory key) {
            cache.refresh(key);
            return get(key);
        }

        @Override
        public void refresh() {

        }

        @Override
        public void clear() {
            Logger.d("clearing BK");
            cache.cleanUp();
            cache.invalidateAll();
        }
    }


}
