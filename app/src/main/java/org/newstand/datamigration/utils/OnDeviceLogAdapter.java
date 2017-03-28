package org.newstand.datamigration.utils;

import android.util.Log;

import com.orhanobut.logger.LogAdapter;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick@NewStand.org on 2017/3/24 15:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OnDeviceLogAdapter implements LogAdapter {

    private static Session session = Session.create();

    private ExecutorService mWriterService;

    public OnDeviceLogAdapter() {
        mWriterService = Executors.newCachedThreadPool();
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.DEBUG));
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.ERROR));
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.WARN));
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.INFO));
    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.VERBOSE));
    }

    @Override
    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
        mWriterService.execute(new Writer(tag, message, Log.ERROR));
    }

    private static class Writer implements Runnable {

        String message;
        String tag;

        int level;

        static final String LOG_DIR = SettingsProvider.getLogDir();

        public Writer(String tag, String message, int level) {
            this.message = message;
            this.tag = tag;
            this.level = level;
        }

        @Override
        public void run() {
            try {
                File logFile = new File(LOG_DIR + File.separator + session.getName() + File.separator + "message");
                com.google.common.io.Files.createParentDirs(logFile);
                PrintWriter printWriter = new PrintWriter(logFile);
                printWriter.println(message);
                printWriter.flush();
                printWriter.close();
            } catch (IOException ignored) {
                Log.d("Nick", Log.getStackTraceString(ignored));
            }
        }

        private static String buildTag(int level, String tag) {
            switch (level) {
                case Log.ASSERT:
                    return "ASSERT--" + tag + "--";
            }
            return "";
        }
    }
}
