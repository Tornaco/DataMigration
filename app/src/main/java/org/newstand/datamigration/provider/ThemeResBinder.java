package org.newstand.datamigration.provider;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

/**
 * Created by Nick@NewStand.org on 2017/4/19 11:02
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ThemeResBinder {
    @StringRes
    int nameRes();

    @ColorRes
    int colorRes();
}
