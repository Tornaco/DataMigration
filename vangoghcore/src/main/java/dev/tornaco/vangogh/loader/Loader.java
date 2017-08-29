package dev.tornaco.vangogh.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */

public interface Loader<T> {
    @Nullable
    T load(@NonNull ImageSource source, @Nullable LoaderObserver observer);

    int priority();
}
