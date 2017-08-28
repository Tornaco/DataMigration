package dev.tornaco.vangogh;

import android.content.Context;

import junit.framework.Assert;

import java.io.File;

/**
 * Created by guohao4 on 2017/8/27.
 * Email: Tornaco@163.com
 */

public class VangoghContext {

    private static Context sContext;

    private static File sDiskCacheDir;

    private static int sRequestPoolSize = Runtime.getRuntime().availableProcessors() / 4 + 1;

    private static int sMemCachePoolSize = (int) (Runtime.getRuntime().maxMemory() / 12);

    static void setContext(Context context) {
        Assert.assertNotNull(context);
        VangoghContext.sContext = context;
    }

    static void setMemCachePoolSize(int sMemCachePoolSize) {
        VangoghContext.sMemCachePoolSize = sMemCachePoolSize;
    }

    public static int getMemCachePoolSize() {
        return sMemCachePoolSize;
    }

    static void setDiskCacheDir(File diskCacheDir) {
        VangoghContext.sDiskCacheDir = diskCacheDir;
    }

    static void setRequestPoolSize(int sRequestPoolSize) {
        VangoghContext.sRequestPoolSize = sRequestPoolSize;
    }

    public static Context getContext() {
        Assert.assertNotNull("Context has not set", sContext);
        return sContext;
    }

    public synchronized static File getDiskCacheDir() {
        if (sDiskCacheDir == null) {
            sDiskCacheDir = new File(getContext().getCacheDir().getPath()
                    + File.separator + "disk_cache");
        }
        return sDiskCacheDir;
    }

    public static int getRequestPoolSize() {
        return sRequestPoolSize;
    }
}
