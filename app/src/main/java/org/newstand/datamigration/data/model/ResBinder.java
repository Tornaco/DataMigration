package org.newstand.datamigration.data.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * Created by Nick@NewStand.org on 2017/3/7 17:42
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ResBinder {
    @StringRes
    int nameRes();

    @DrawableRes
    int iconRes();
}
