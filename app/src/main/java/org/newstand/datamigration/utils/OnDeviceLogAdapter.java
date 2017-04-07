package org.newstand.datamigration.utils;

import android.util.Log;

import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.logger.FastPrintWriter;
import org.newstand.logger.LogAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick@NewStand.org on 2017/3/24 15:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OnDeviceLogAdapter implements LogAdapter, Closeable {

    private FastPrintWriter fw;

    private ExecutorService exe = Executors.newSingleThreadExecutor();

    public OnDeviceLogAdapter(int bufferSize) {
        StringWriter sw = new StringWriter() {
            @Override
            public void flush() {
                super.flush();
            }
        };
        fw = new FastPrintWriter(sw, false, bufferSize);
    }

    public OnDeviceLogAdapter() {
        this(8192);
    }

    @Override
    public void d(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(tag, message);
                fw.println("DEBUG\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void e(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(tag, message);
                fw.println("ERROR\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void w(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.w(tag, message);
                fw.println("WARN\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void i(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, message);
                fw.println("INFO\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void v(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.v(tag, message);
                fw.println("VERBOSE\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void wtf(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.wtf(tag, message);
                fw.println("WTF\t" + DateUtils.formatLong(System.currentTimeMillis()) + tag + ":\t" + message);
            }
        });
    }

    @Override
    public void close() throws IOException {
        fw.close();
    }
}
