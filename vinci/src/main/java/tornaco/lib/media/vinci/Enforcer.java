package tornaco.lib.media.vinci;

/**
 * Created by Nick on 2017/5/5 11:19
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Enforcer {

    public static <T> T enforceNonNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static void enforce(boolean arg, String message) {
        if (!arg) throw new IllegalArgumentException(message);
    }
}
