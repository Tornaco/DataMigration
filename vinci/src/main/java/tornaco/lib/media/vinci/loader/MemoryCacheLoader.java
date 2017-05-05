package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import lombok.Getter;
import tornaco.lib.media.vinci.cache.MemoryCache;
import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.policy.CacheKeyPolicy;

/**
 * Created by Nick on 2017/5/5 12:54
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
public class MemoryCacheLoader implements Loader, Consumer<OnLoadCompleteEvent> {

    private MemoryCache cache;
    private CacheKeyPolicy policy;

    public MemoryCacheLoader(int cachePoolSize, CacheKeyPolicy cacheKeyPolicy) {
        cache = new MemoryCache(cachePoolSize);
        policy = cacheKeyPolicy;

        LoaderEventProvider.getInstance().subscribe(new Consumer<OnLoadCompleteEvent>() {
            @Override
            public void accept(OnLoadCompleteEvent onLoadCompleteEvent) {
                if (onLoadCompleteEvent.getImage() != null) {
                    Loader who = onLoadCompleteEvent.getWho();
                    if (who != MemoryCacheLoader.this) {
                        // Put this entry.
                        cache.put(policy.createCacheKey(onLoadCompleteEvent.getSourceUrl()), onLoadCompleteEvent.getImage());
                    }
                }
            }
        });
    }

    @WorkerThread
    @Nullable
    @Override
    public Bitmap load(@NonNull String sourceUrl) {
        // Retrieve with cache key.
        return cache.get(policy.createCacheKey(sourceUrl));
    }

    @Override
    public int priority() {
        return Priority.A;
    }

    @Override
    public void accept(OnLoadCompleteEvent onLoadCompleteEvent) {
        Bitmap image = onLoadCompleteEvent.getImage();
        if (image != null) {
            String url = onLoadCompleteEvent.getSourceUrl();
            cache.put(policy.createCacheKey(url), image);
        }
    }
}
