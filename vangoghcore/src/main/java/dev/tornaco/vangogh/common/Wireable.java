package dev.tornaco.vangogh.common;

import android.support.annotation.NonNull;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

public interface Wireable<T> {
    void wire(@NonNull T t);
}
