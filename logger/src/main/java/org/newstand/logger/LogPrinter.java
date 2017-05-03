package org.newstand.logger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class LogPrinter implements Printer {

    private String tag;
    private LogAdapter adapter;
    private Logger.LogLevel level;

    private ExecutorService exe = Executors.newSingleThreadExecutor();

    private PrintStream directStream;

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
        if (level.ordinal() <= Logger.LogLevel.DEBUG.ordinal()) {
            String msg = String.format(message, args);
            adapter.d(tag, msg);
            exe.execute(new Directer(tag, msg));
        }
    }

    @Override
    public void d(Object object) {
        if (level.ordinal() <= Logger.LogLevel.DEBUG.ordinal()) {
            adapter.d(tag, String.valueOf(object));
            exe.execute(new Directer(tag, String.valueOf(object)));
        }
    }

    @Override
    public void e(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.ERROR.ordinal()) {
            String msg = String.format(message, args);
            adapter.e(tag, msg);
            exe.execute(new Directer(tag, msg));
        }
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.ERROR.ordinal()) {
            String err = String.format(message, args) + "\n" + getStackTraceString(throwable);
            adapter.e(tag, err);
            exe.execute(new Directer(tag, err));
        }
    }

    @Override
    public void w(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.WARN.ordinal()) {
            String wn = String.format(message, args);
            adapter.w(tag, wn);
            exe.execute(new Directer(tag, wn));
        }
    }

    @Override
    public void i(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.INFO.ordinal()) {
            String info = String.format(message, args);
            adapter.i(tag, info);
            exe.execute(new Directer(tag, info));
        }
    }

    @Override
    public void v(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.VERBOSE.ordinal()) {
            String ver = String.format(message, args);
            adapter.v(tag, ver);
            exe.execute(new Directer(tag, ver));
        }
    }

    @Override
    public void wtf(String message, Object... args) {
        if (level.ordinal() <= Logger.LogLevel.WARN.ordinal()) {
            String wf = String.format(message, args);
            adapter.wtf(tag, wf);
            exe.execute(new Directer(tag, wf));
        }
    }

    @Override
    public void startRedirection(PrintStream ps) {
        stopRedirection();
        synchronized (this) {
            directStream = ps;
        }
    }

    @Override
    public void stopRedirection() {
        synchronized (this) {
            if (directStream != null) {
                try {
                    directStream.close();
                } catch (Exception ignored) {
                }
                directStream = null;
            }
        }
    }

    private void directToStream(String tag, String message) {
        String line = tag + "---" + message;
        synchronized (this) {
            if (directStream != null) {
                directStream.println(line);
                directStream.flush();
            }
        }
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

    private class Directer implements Runnable {

        String tag, message;

        Directer(String tag, String message) {
            this.tag = tag;
            this.message = message;
        }

        @Override
        public void run() {
            directToStream(tag, message);
        }
    }
}
