package tornaco.lib.media.vinci.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nick on 2017/5/5 13:04
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface Cache<K, V> {
    @Nullable
    V get(@NonNull K key);

    @NonNull
    V put(@NonNull K key, @NonNull V value);

    boolean has(@NonNull K key);

    /**
     * Clear all and release memory.
     */
    void evictAll();
}
