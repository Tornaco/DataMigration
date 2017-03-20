package org.newstand.datamigration.sync;

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

}
