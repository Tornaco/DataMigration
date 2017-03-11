package org.newstand.datamigration.common;

import android.support.annotation.NonNull;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Consumer<T> {
    void consume(@NonNull T t);
}
