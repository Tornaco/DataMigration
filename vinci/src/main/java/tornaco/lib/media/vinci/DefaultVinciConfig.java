package tornaco.lib.media.vinci;

import android.content.Context;

import java.io.File;

import tornaco.lib.media.vinci.policy.HashCodeCacheKeyPolicy;
import tornaco.lib.media.vinci.policy.HeadlessCacheKeyPolicy;

/**
 * Created by Nick on 2017/5/5 15:21
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class DefaultVinciConfig extends VinciConfig {

    public DefaultVinciConfig(Context context) {
        super(false, false,
                new File(context.getCacheDir() + File.separator + "disk_cache"),
                (int) (Runtime.getRuntime().maxMemory() / 8),
                new HashCodeCacheKeyPolicy(), new HeadlessCacheKeyPolicy());
    }
}
