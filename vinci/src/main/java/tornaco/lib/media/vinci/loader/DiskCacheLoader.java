package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.File;

import tornaco.lib.media.vinci.cache.DiskCache;
import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.policy.CacheKeyPolicy;

/**
 * Created by Nick on 2017/5/5 12:50
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class DiskCacheLoader implements Loader {

    private DiskCache mDiskCache;
    private CacheKeyPolicy mCacheKeyPolicy;

    public DiskCacheLoader(File rootDir, CacheKeyPolicy cacheKeyPolicy) {
        mDiskCache = new DiskCache(rootDir);
        mCacheKeyPolicy = cacheKeyPolicy;

        LoaderEventProvider.getInstance().subscribe(new Consumer<OnLoadCompleteEvent>() {
            @Override
            public void accept(OnLoadCompleteEvent onLoadCompleteEvent) {

                Bitmap res = onLoadCompleteEvent.getImage();
                if (res != null) {
                    Loader who = onLoadCompleteEvent.getWho();
                    if (who != DiskCacheLoader.this && who.getClass() != MemoryCacheLoader.class) {
                        // Add this entry.
                        mDiskCache.put(mCacheKeyPolicy.createCacheKey(onLoadCompleteEvent.getSourceUrl()),
                                onLoadCompleteEvent.getImage());
                    }
                }
            }
        });
    }

    @WorkerThread
    @Nullable
    @Override
    public Bitmap load(@NonNull String sourceUrl) {
        return mDiskCache.get(mCacheKeyPolicy.createCacheKey(sourceUrl));
    }

    @Override
    public int priority() {
        return Priority.B;
    }
}
