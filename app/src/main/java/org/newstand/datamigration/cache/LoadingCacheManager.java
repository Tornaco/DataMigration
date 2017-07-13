package org.newstand.datamigration.cache;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.newstand.datamigration.loader.LoaderSource.Parent.Android;

/**
 * Created by Nick@NewStand.org on 2017/3/23 10:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class LoadingCacheManager {

    private static LoadingCacheManager droid, bk, received;

    public static LoadingCacheManager droid() {
        return droid;
    }

    public static LoadingCacheManager bk() {
        return bk;
    }

    public static LoadingCacheManager received() {
        return received;
    }

    public static void createDroid(Context appContext) {
        Droid.create(appContext);
        droid = Droid.sMe;
    }

    public static void createBK(Context appContext, Session session) {
        BK.create(appContext, session);
        bk = BK.sMe;
    }

    public static void createReceived(Context appContext, Session session) {
        Received.create(appContext, session);
        received = Received.sMe;
    }

    public abstract
    @NonNull
    Collection<DataRecord> get(DataCategory key);

    public
    @NonNull
    Collection<DataRecord> checked(DataCategory key) {
        Collection<DataRecord> all = get(key);

        if (Collections.isNullOrEmpty(all)) return all;

        final List<DataRecord> checked = new ArrayList<>(all.size());

        Collections.consumeRemaining(all, new Consumer<DataRecord>() {
            @Override
            public void accept(@NonNull DataRecord dataRecord) {
                if (dataRecord.isChecked()) checked.add(dataRecord);
            }
        });
        return checked;
    }

    public abstract
    @NonNull
    Collection<DataRecord> getRefreshed(DataCategory key);

    public abstract void refresh(DataCategory key);

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
                            // Workaround to avoid sup loading.
                            if (!SettingsProvider.isLoadEnabledForCategory(key)) {
                                return new ArrayList<>();
                            }

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
                Logger.e(e, "Fail to get delegate cache: %s", key);
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
        public void refresh(DataCategory key) {
            cache.refresh(key);
        }

        @Override
        public void clear() {
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
                Logger.e(e, "Fail to get delegate cache: %s", key);
                return java.util.Collections.emptyList();
            } catch (CacheLoader.InvalidCacheLoadException e) {
                Logger.e(e, "Fail to get delegate cache: %s", key);
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
        public void refresh(DataCategory key) {
            cache.refresh(key);
        }

        @Override
        public void clear() {
            cache.cleanUp();
            cache.invalidateAll();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static class Received extends LoadingCacheManager {

        private LoadingCache<DataCategory, Collection<DataRecord>> cache;

        private static LoadingCacheManager sMe;

        public Received(final Context context, final Session session) {
            cache = CacheBuilder.newBuilder().maximumSize(DataCategory.values().length)
                    .expireAfterAccess(365, TimeUnit.DAYS)
                    .build(new CacheLoader<DataCategory, Collection<DataRecord>>() {
                        @Override
                        public Collection<DataRecord> load(@NonNull DataCategory key) throws Exception {
                            DataLoaderManager manager = DataLoaderManager.from(context);
                            return manager.load(LoaderSource.builder().parent(LoaderSource.Parent.Received)
                                    .session(session).build(), key);
                        }
                    });
        }

        public static void create(Context context, Session session) {
            if (sMe != null) sMe.clear();
            Preconditions.checkNotNull(session);
            sMe = new Received(context, session);
        }

        @NonNull
        @Override
        public Collection<DataRecord> get(DataCategory key) {
            try {
                return cache.get(key);
            } catch (ExecutionException e) {
                Logger.e(e, "Fail to get delegate cache: %s", key);
                return java.util.Collections.emptyList();
            } catch (CacheLoader.InvalidCacheLoadException e) {
                Logger.e(e, "Fail to get delegate cache: %s", key);
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
        public void refresh(DataCategory key) {
            cache.refresh(key);
        }

        @Override
        public void clear() {
            cache.cleanUp();
            cache.invalidateAll();
        }
    }


}
