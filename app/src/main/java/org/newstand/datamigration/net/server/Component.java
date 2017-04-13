package org.newstand.datamigration.net.server;

import org.newstand.datamigration.common.Startable;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Component extends Startable {

    String name();
}
