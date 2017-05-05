package tornaco.lib.media.vinci.policy;

import android.support.annotation.NonNull;

import tornaco.lib.media.vinci.Enforcer;

/**
 * Created by Nick on 2017/5/5 13:18
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class HeadlessCacheKeyPolicy implements CacheKeyPolicy {
    @Override
    public String createCacheKey(@NonNull String sourceUrl) {
        return Enforcer.enforceNonNull(sourceUrl);
    }
}
