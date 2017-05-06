package tornaco.lib.media.vinci.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Nick on 2017/5/6 13:03
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Closer {
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignore) {
            Logger.d("Close %s with err", Logger.getStackTraceString(ignore));
        }
    }
}
