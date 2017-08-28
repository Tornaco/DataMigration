package dev.tornaco.vangogh.loader.cache;

import junit.framework.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;
import lombok.Getter;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

public class CacheManager implements Closeable {

    @Getter
    Cache<ImageSource, Image> diskCache, memCache;

    private static CacheManager cacheManager;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private CacheManager(Cache<ImageSource, Image> diskCache, Cache<ImageSource, Image> memCache) {
        this.diskCache = diskCache;
        this.memCache = memCache;
    }

    public static CacheManager getInstance() {
        Assert.assertNotNull("CacheManager not init", cacheManager);
        return cacheManager;
    }

    public static void init(CachePolicy cachePolicy) {
        cacheManager = new CacheManager(DiskCache.init(cachePolicy), MemoryCache.init(cachePolicy));
    }

    public void onImageReady(final ImageSource source, final Image image) {
        if (image.asBitmap() == null) return;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                memCache.put(source, image);
                diskCache.put(source, image);
            }
        });
    }

    @Override
    public void close() throws IOException {
        executorService.shutdownNow();
    }
}
