package org.newstand.lib.util;

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

    @Override
    public void set(Settings settings) {
        tag = settings.getTag();
        adapter = settings.getLogAdapter();
    }

    @Override
    public void setLogAdapter(LogAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void d(String message, Object... args) {
        adapter.d(tag, String.format(message, args));
    }

    @Override
    public void d(Object object) {
        adapter.d(tag, String.valueOf(object));
    }

    @Override
    public void e(String message, Object... args) {
        adapter.e(tag, String.format(message, args));
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        adapter.e(tag, String.format(message, args) + "\n" + getStackTraceString(throwable));
    }

    @Override
    public void w(String message, Object... args) {
        adapter.w(tag, String.format(message, args));
    }

    @Override
    public void i(String message, Object... args) {
        adapter.i(tag, String.format(message, args));
    }

    @Override
    public void v(String message, Object... args) {
        adapter.v(tag, String.format(message, args));
    }

    @Override
    public void wtf(String message, Object... args) {
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
