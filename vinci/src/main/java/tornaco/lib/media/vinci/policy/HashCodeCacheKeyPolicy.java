package tornaco.lib.media.vinci.policy;

import android.support.annotation.NonNull;

import tornaco.lib.media.vinci.Enforcer;

/**
 * Created by Nick on 2017/5/5 13:15
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class HashCodeCacheKeyPolicy implements CacheKeyPolicy {
    @Override
    public String createCacheKey(@NonNull String sourceUrl) {
        return Integer.toHexString(Enforcer.enforceNonNull(sourceUrl).hashCode());
    }
}
