package org.newstand.logger;

import com.bugsnag.android.Bugsnag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Logger {

    public static enum LogLevel {
        ALL,
        VERBOSE,
        INFO,
        DEBUG,
        WARN,
        ERROR,
        NONE
    }

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
        Bugsnag.notify(throwable);
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

    public static String getStackTraceString(Throwable throwable) {
        if (throwable == null) return "";
        Throwable t = throwable;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new FastPrintWriter(stringWriter, false, 256);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }
}
