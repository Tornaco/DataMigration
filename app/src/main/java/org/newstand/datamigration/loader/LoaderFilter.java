package org.newstand.datamigration.loader;


import android.support.annotation.NonNull;

/**
 * Created by Nick@NewStand.org on 2017/3/7 10:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface LoaderFilter<T> {
    boolean ignored(@NonNull T ths);
}
