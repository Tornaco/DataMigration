package tornaco.lib.media.vinci.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick on 2017/5/5 22:38
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
@Setter
public class Holder<T> {
    private T host;

    public Holder(T host) {
        this.host = host;
    }
}
