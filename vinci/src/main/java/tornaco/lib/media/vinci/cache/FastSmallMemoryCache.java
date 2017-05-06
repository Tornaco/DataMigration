package tornaco.lib.media.vinci.cache;

/**
 * Created by Nick on 2017/5/6 11:43
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class FastSmallMemoryCache extends MemoryCache {

    private static final FastSmallMemoryCache sMe =
            new FastSmallMemoryCache((int) (Runtime.getRuntime().maxMemory() / 12));

    private FastSmallMemoryCache(int poolSize) {
        super(poolSize);
    }

    public static FastSmallMemoryCache get() {
        return sMe;
    }
}
