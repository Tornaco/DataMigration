package org.newstand.logger;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface LogAdapter {
    void d(String tag, String message);

    void e(String tag, String message);

    void w(String tag, String message);

    void i(String tag, String message);

    void v(String tag, String message);

    void wtf(String tag, String message);
}
