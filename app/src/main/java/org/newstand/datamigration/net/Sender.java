package org.newstand.datamigration.net;

import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Sender<T> extends IO {

    int send(T t) throws IOException;
}
