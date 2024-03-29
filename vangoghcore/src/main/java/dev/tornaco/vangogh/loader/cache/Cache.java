package dev.tornaco.vangogh.loader.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

interface Cache<K, V> {
    @Nullable
    V get(@NonNull K k);

    boolean put(@NonNull K k, @NonNull V v);

    void clear();
}
