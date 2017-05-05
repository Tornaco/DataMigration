package tornaco.lib.media.vinci.common;

/**
 * Created by Nick on 2017/5/5 14:19
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);
}