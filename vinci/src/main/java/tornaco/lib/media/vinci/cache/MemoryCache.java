package tornaco.lib.media.vinci.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import tornaco.lib.media.vinci.Enforcer;

/**
 * Created by Nick on 2017/5/5 13:07
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class MemoryCache implements Cache<String, Bitmap> {

    private LruCache<String, Bitmap> mLruCache;

    public MemoryCache(int poolSize) {
        mLruCache = new LruCache<String, Bitmap>(poolSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (value == null) return 0;
                return value.getWidth() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                // Hard to recycle.
                oldValue = null;
            }
        };
    }

    @Nullable
    @Override
    public Bitmap get(@NonNull String key) {
        return mLruCache.get(Enforcer.enforceNonNull(key));
    }

    @NonNull
    @Override
    public Bitmap put(@NonNull String key, @NonNull Bitmap value) {
        return mLruCache.put(Enforcer.enforceNonNull(key), Enforcer.enforceNonNull(value));
    }

    @Override
    public boolean has(@NonNull String key) {
        return get(key) != null;
    }

    @Override
    public void evictAll() {
        mLruCache.evictAll();
    }
}
