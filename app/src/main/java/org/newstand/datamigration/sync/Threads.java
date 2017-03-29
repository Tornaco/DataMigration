package org.newstand.datamigration.sync;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Nick@NewStand.org on 2017/3/29 12:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Threads {

    private static final AtomicInteger sThreadID = new AtomicInteger(0);

    public static Thread started(Runnable r) {
        Thread t = new Thread(r);
        t.setName("App-Thread-" + sThreadID.getAndIncrement());
        t.start();
        return t;
    }
}
