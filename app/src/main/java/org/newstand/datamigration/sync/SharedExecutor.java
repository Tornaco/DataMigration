package org.newstand.datamigration.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick@NewStand.org on 2017/3/7 12:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class SharedExecutor {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public static ExecutorService getService() {
        return executorService;
    }
}
