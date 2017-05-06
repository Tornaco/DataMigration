package tornaco.lib.media.vinci.utils;

/**
 * Created by Nick on 2017/5/5 17:05
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Logger {

    public static void d(String message, Object... args) {
        android.util.Log.d("Vinci", String.format(message, args));
    }

    public static String getStackTraceString(Throwable throwable) {
        return android.util.Log.getStackTraceString(throwable);
    }
}
