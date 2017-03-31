package org.newstand.lib;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Printer {

    Printer t(String tag, int methodCount);

    void d(String message, Object... args);

    void d(Object object);

    void e(String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);

    void json(String json);

    void xml(String xml);

    void log(int priority, String tag, String message, Throwable throwable);
}
