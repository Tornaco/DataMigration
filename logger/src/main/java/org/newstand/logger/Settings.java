package org.newstand.logger;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Settings {

    private int logLevel;
    private LogAdapter logAdapter;
    private String tag;

    private Settings(int logLevel, LogAdapter logAdapter, String tag) {
        this.logLevel = logLevel;
        this.logAdapter = logAdapter;
        this.tag = tag;
    }

    public static SettingsBuilder builder() {
        return new SettingsBuilder();
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public LogAdapter getLogAdapter() {
        return this.logAdapter;
    }

    public String getTag() {
        return this.tag;
    }

    public static class SettingsBuilder {

        private int logLevel = -1;
        private LogAdapter logAdapter = null;
        private String tag = "Logger";

        SettingsBuilder() {
        }

        public Settings.SettingsBuilder logLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Settings.SettingsBuilder logAdapter(LogAdapter logAdapter) {
            this.logAdapter = logAdapter;
            return this;
        }

        public Settings.SettingsBuilder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Settings build() {
            if (logAdapter == null) logAdapter = new AndroidLogAdapter();
            return new Settings(logLevel, logAdapter, tag);
        }
    }
}
