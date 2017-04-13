package org.newstand.datamigration.common;

import org.newstand.lib.iface.Adapter;

/**
 * Created by Nick@NewStand.org on 2017/4/13 17:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public interface Startable {
    boolean start();

    boolean stop();
}
