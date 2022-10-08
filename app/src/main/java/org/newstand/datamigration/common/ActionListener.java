package org.newstand.datamigration.common;

import androidx.annotation.Nullable;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ActionListener<T> {
    void onAction(@Nullable T t);
}
