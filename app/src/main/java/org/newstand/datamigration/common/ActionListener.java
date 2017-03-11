package org.newstand.datamigration.common;

import android.support.annotation.Nullable;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ActionListener<T> {
    void onAction(@Nullable T t);
}
