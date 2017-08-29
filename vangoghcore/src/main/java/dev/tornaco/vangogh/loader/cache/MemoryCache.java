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

class MemoryCache implements Cache<ImageSource, Image> {

    private LruCache<ImageSource, Image> mLruCache;

    MemoryCache(int poolSize) {
        mLruCache = new LruCache<ImageSource, Image>(poolSize) {
            @SuppressWarnings("ConstantConditions")
            @Override
            protected int sizeOf(ImageSource key, Image value) {
                if (value == null || value.asBitmap(key.getContext()) == null) return 0;
                return value.asBitmap(key.getContext()).getWidth() * value.asBitmap(key.getContext()).getHeight();
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
        if (image.asBitmap(source.getContext()) == null)
            return false;
        mLruCache.put(source, image);
        return true;
    }

    @Override
    public void clear() {

    }
}
