package org.newstand.datamigration.net;

import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/3/22 11:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Receiver<T> extends IORES {
    int receive(T t) throws IOException;
}
