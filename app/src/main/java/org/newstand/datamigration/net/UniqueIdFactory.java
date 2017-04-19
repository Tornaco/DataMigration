package org.newstand.datamigration.net;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nick@NewStand.org on 2017/4/19 15:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UniqueIdFactory {

    private static AtomicInteger sId = new AtomicInteger(0);

    public static int next() {
        return sId.incrementAndGet();
    }
}
