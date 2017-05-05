package tornaco.lib.media.vinci.common;

/**
 * Created by Nick on 2017/5/5 15:41
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public abstract class Consumer2<T> implements Consumer<T> {
    public abstract void andThen(Consumer<T> consumer);
}
