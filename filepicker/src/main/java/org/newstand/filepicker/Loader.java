package org.newstand.filepicker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nick@NewStand.org on 2017/3/31 10:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Loader<RES, PARAM>  extends Abortable {
    @NonNull
    RES load(@Nullable PARAM p);
}
