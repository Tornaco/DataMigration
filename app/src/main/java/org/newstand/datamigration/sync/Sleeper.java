package org.newstand.datamigration.sync;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Nick@NewStand.org on 2017/3/10 10:10
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class Sleeper {

    public static void sleepQuietly(long timeMills) {
        try {
            Thread.sleep(timeMills);
        } catch (InterruptedException ignored) {

        }
    }

    public static void sleepQuietly() {
        sleepQuietly(500);
    }

    public static CountDownLatch waitingFor(int times, final Runnable onWakeup) {
        final CountDownLatch latch = new CountDownLatch(times);
        Threads.started(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        latch.await();
                        onWakeup.run();
                        break;
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
        return latch;
    }

}
