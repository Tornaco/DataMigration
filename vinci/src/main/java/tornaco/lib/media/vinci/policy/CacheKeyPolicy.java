package tornaco.lib.media.vinci.policy;

import android.support.annotation.NonNull;

/**
 * Created by Nick on 2017/5/5 13:14
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface CacheKeyPolicy {
    String createCacheKey(@NonNull String sourceUrl);
}
