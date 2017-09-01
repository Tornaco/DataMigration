package org.newstand.datamigration.utils;

import android.util.Log;

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

    private ExecutorService exe = Executors.newSingleThreadExecutor();

    @Override
    public void d(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(tag, message);
            }
        });
    }

    @Override
    public void e(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(tag, message);
            }
        });
    }

    @Override
    public void w(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.w(tag, message);
            }
        });
    }

    @Override
    public void i(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, message);
            }
        });
    }

    @Override
    public void v(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, message);
            }
        });
    }

    @Override
    public void wtf(final String tag, final String message) {
        exe.execute(new Runnable() {
            @Override
            public void run() {
                Log.wtf(tag, message);
            }
        });
    }

    @Override
    public void close() throws IOException {
        exe.shutdownNow();
    }
}
