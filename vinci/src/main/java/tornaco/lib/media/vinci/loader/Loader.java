package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

/**
 * Created by Nick on 2017/5/5 12:27
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface Loader {
    @Nullable
    @WorkerThread
    Bitmap load(@NonNull String sourceUrl);

    int priority();
}
