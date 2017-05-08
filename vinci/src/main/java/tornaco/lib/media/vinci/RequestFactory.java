package tornaco.lib.media.vinci;

import android.content.Context;

import com.google.common.collect.Lists;

import java.util.List;

import tornaco.lib.media.vinci.display.ImageConsumer;
import tornaco.lib.media.vinci.loader.DiskCacheLoader;
import tornaco.lib.media.vinci.loader.MemoryCacheLoader;
import tornaco.lib.media.vinci.loader.sources.ImageSourcesLoader;

/**
 * Created by Nick on 2017/5/5 13:43
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class RequestFactory {

    private MemoryCacheLoader memCacheLoader;
    private DiskCacheLoader mDiskCacheLoader;

    private boolean enableDiskCache, enableMemoryCache;

    private static RequestFactory sFactory;


    private RequestFactory(VinciConfig config) {
        mDiskCacheLoader = new DiskCacheLoader(config.getDiskCacheDir(), config.getDiskCacheKeyPolicy());
        memCacheLoader = new MemoryCacheLoader(config.getMemCachePoolSize(), config.getMemoryCacheKeyPolicy());
        enableDiskCache = config.isEnableDiskCache();
        enableMemoryCache = config.isEnableMemoryCache();
    }

    private static RequestFactory getFactory() {
        return sFactory;
    }

    private DiskCacheLoader getDiskCacheLoader() {
        return mDiskCacheLoader;
    }

    private MemoryCacheLoader getMemCacheLoader() {
        return memCacheLoader;
    }

    static Request newRequest(Context context, final String sourceUrl) {
        List<ImageConsumer> initial = Lists.newArrayList();
        Request r = new Request(context, sourceUrl, initial);
        if (getFactory().enableMemoryCache) r.loader(getFactory().getMemCacheLoader());
        if (getFactory().enableDiskCache) r.loader(getFactory().getDiskCacheLoader());
        r.loader(new ImageSourcesLoader(context));
        return r;
    }

    public static void init(VinciConfig config) {
        sFactory = new RequestFactory(config);
    }
}
