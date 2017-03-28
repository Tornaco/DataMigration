package org.newstand.datamigration.common;

/**
 * Created by Nick@NewStand.org on 2017/3/28 13:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Producer<T, X> {
    T produce(X source);
}
