package tornaco.lib.media.vinci.loader.sources;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nick on 2017/5/5 14:33
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

interface SourceLoader {
    @Nullable
    Bitmap loadFromSource(@NonNull Source source);

    boolean canHandle(@NonNull String sourceUrl);
}
