package dev.tornaco.vangogh.request;

import androidx.annotation.NonNull;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */

public interface Dispatcher<T> {

    void dispatch(@NonNull T t);

    boolean cancel(@NonNull T t, boolean interruptRunning);

    void cancelAll(boolean interruptRunning);

    void quit();
}
