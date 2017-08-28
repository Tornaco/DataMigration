package dev.tornaco.vangogh.loader.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import junit.framework.Assert;

import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

public class MemoryCache implements Cache<ImageSource, Image> {

    private static MemoryCache sMe;

    static synchronized MemoryCache init(CachePolicy cachePolicy) {
        if (sMe == null) sMe = new MemoryCache(cachePolicy);
        return sMe;
    }

    private LruCache<ImageSource, Image> mLruCache;

    public MemoryCache(CachePolicy cachePolicy) {
        long poolSize = cachePolicy.getMemCacheSize();
        mLruCache = new LruCache<ImageSource, Image>((int) poolSize) {
            @Override
            protected int sizeOf(ImageSource key, Image value) {
                if (value == null || value.asBitmap() == null) return 0;
                return value.asBitmap().getWidth() * value.asBitmap().getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, ImageSource key, Image oldValue, Image newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                oldValue = null;
            }
        };
    }

    @Nullable
    @Override
    public Image get(@NonNull ImageSource source) {
        Assert.assertNotNull("Source is null", source);
        return mLruCache.get(source);
    }

    @Override
    public boolean put(@NonNull ImageSource source, @NonNull Image image) {
        Assert.assertNotNull(source);
        Assert.assertNotNull(image);
        if (image.asBitmap() == null)
            return false;
        mLruCache.put(source, image);
        return true;
    }

    @Override
    public void clear() {

    }
}
