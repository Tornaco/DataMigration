package tornaco.lib.media.vinci;

/**
 * Created by Nick on 2017/5/5 13:27
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class ErrorReporter {
    public static void reThrow(Throwable e) {
        throw new RuntimeException(e);
    }
}
