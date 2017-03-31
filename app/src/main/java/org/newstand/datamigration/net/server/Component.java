package org.newstand.datamigration.net.server;

import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Component {
    boolean start() throws IOException;

    boolean stop();

    String name();
}
