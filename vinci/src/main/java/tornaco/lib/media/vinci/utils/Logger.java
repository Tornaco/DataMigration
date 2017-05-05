package tornaco.lib.media.vinci.utils;

/**
 * Created by Nick on 2017/5/5 17:05
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Logger {
    public static void dbg(String message, Object... args) {
        org.newstand.logger.Logger.d(message, args);
    }
}
