package org.newstand.logger;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Logger {

    private static Printer printer;

    public static void config(Settings settings) {
        printer = new LogPrinter();
        printer.set(settings);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void d(Object object) {
        printer.d(object);
    }

    public static void e(String message, Object... args) {
        printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }
}
