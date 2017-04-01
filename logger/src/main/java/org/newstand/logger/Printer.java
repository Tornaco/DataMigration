package org.newstand.logger;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

interface Printer {

    void set(Settings settings);

    void setLogAdapter(LogAdapter adapter);

    void d(String message, Object... args);

    void d(Object object);

    void e(String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);
}
