package org.newstand.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class LogPrinter implements Printer {

    private String tag;
    private LogAdapter adapter;
    private Logger.LogLevel level;

    @Override
    public void set(Settings settings) {
        tag = settings.getTag();
        adapter = settings.getLogAdapter();
        level = settings.getLogLevel();
    }

    @Override
    public void setLogAdapter(LogAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void d(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.DEBUG.ordinal())
            adapter.d(tag, String.format(message, args));
    }

    @Override
    public void d(Object object) {
        if (level.ordinal() <= Logger.LogLevel.DEBUG.ordinal())
            adapter.d(tag, String.valueOf(object));
    }

    @Override
    public void e(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.ERROR.ordinal())
            adapter.e(tag, String.format(message, args));
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.ERROR.ordinal())
            adapter.e(tag, String.format(message, args) + "\n" + getStackTraceString(throwable));
    }

    @Override
    public void w(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.WARN.ordinal())
            adapter.w(tag, String.format(message, args));
    }

    @Override
    public void i(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.INFO.ordinal())
            adapter.i(tag, String.format(message, args));
    }

    @Override
    public void v(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.VERBOSE.ordinal())
            adapter.v(tag, String.format(message, args));
    }

    @Override
    public void wtf(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.WARN.ordinal())
            adapter.wtf(tag, String.format(message, args));
    }

    private static String getStackTraceString(Throwable throwable) {
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
