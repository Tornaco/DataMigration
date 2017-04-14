package org.newstand.datamigration.common;

import org.newstand.lib.iface.Adapter;

/**
 * Created by Nick@NewStand.org on 2017/4/14 10:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Adapter
public interface IFaceGType<T> {
    T get();
}
