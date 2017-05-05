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

public class RequestFactory {

    private static MemoryCacheLoader sMemCacheLoader;
    private static DiskCacheLoader sDiskCacheLoader;

    private static DiskCacheLoader getDiskCacheLoader() {
        return sDiskCacheLoader;
    }

    private static MemoryCacheLoader getMemCacheLoader() {
        return sMemCacheLoader;
    }

    static Request newRequest(Context context, final String sourceUrl) {
        List<ImageConsumer> initial = Lists.newArrayList();
        Request r = new Request(context, sourceUrl, initial);
        r.loader(getMemCacheLoader());
        r.loader(getDiskCacheLoader());
        r.loader(new ImageSourcesLoader(context));
        return r;
    }

    public static void init(VinciConfig config) {
        sDiskCacheLoader = new DiskCacheLoader(config.getDiskCacheDir(), config.getDiskCacheKeyPolicy());
        sMemCacheLoader = new MemoryCacheLoader(config.getMemCachePoolSize(), config.getMemoryCacheKeyPolicy());
    }
}
