package tornaco.lib.media.vinci.common;

import android.support.annotation.NonNull;

/**
 * Created by Nick on 2017/5/5 16:25
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface Publisher<T> {
    void publish(@NonNull T t);
}
