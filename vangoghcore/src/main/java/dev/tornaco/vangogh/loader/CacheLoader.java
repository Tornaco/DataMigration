package dev.tornaco.vangogh.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dev.tornaco.vangogh.VangoghContext;
import dev.tornaco.vangogh.loader.cache.Cache;
import dev.tornaco.vangogh.loader.cache.CacheManager;
import dev.tornaco.vangogh.loader.cache.CachePolicy;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */

public class CacheLoader extends BaseImageLoader {

    private Cache<ImageSource, Image> memCache;
    private Cache<ImageSource, Image> diskCache;

    public CacheLoader() {
        CacheManager.init((CachePolicy.builder().diskCacheDir(VangoghContext.getDiskCacheDir())
                .memCacheSize(VangoghContext.getMemCachePoolSize()).build()));
        memCache = CacheManager.getInstance().getMemCache();
        diskCache = CacheManager.getInstance().getDiskCache();
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    boolean canHandleType(@Nullable ImageSource.SourceType type) {
        return type != null;
    }

    @Nullable
    @Override
    Image doLoad(@NonNull ImageSource source, @Nullable LoaderObserver observer) {
        Image image = source.isSkipMemoryCache() ? null : memCache.get(source);
        if (image == null) {
            image = source.isSkipDiskCache() ? null : diskCache.get(source);
        }
        return image;
    }
}
