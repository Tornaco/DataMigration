package dev.tornaco.vangogh.loader.cache;

import java.io.File;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */
@Builder
@Getter
public class CachePolicy {
    private long memCacheSize;
    private File diskCacheDir;
}
