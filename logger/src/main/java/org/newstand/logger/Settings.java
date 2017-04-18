package org.newstand.logger;

/**
 * Created by Nick@NewStand.org on 2017/3/31 17:50
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Settings {

    private Logger.LogLevel logLevel;
    private LogAdapter logAdapter;
    private String tag;
    private boolean bugReportEnabled;

    private Settings(Logger.LogLevel logLevel, LogAdapter logAdapter, String tag, boolean bugReportEnabled) {
        this.logLevel = logLevel;
        this.logAdapter = logAdapter;
        this.tag = tag;
        this.bugReportEnabled = bugReportEnabled;
    }

    public static SettingsBuilder builder() {
        return new SettingsBuilder();
    }

    public Logger.LogLevel getLogLevel() {
        return this.logLevel;
    }

    public LogAdapter getLogAdapter() {
        return this.logAdapter;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean isBugReportEnabled() {
        return bugReportEnabled;
    }

    public static class SettingsBuilder {

        private Logger.LogLevel logLevel = Logger.LogLevel.WARN;
        private LogAdapter logAdapter = null;
        private String tag = "Logger";
        private boolean bugReportEnabled = false;

        SettingsBuilder() {
        }

        public Settings.SettingsBuilder logLevel(Logger.LogLevel logLevel) {
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

        public Settings.SettingsBuilder bugReportEnabled(boolean bugReportEnabled) {
            this.bugReportEnabled = bugReportEnabled;
            return this;
        }

        public Settings build() {
            if (logAdapter == null) logAdapter = new AndroidLogAdapter();
            return new Settings(logLevel, logAdapter, tag, bugReportEnabled);
        }
    }
}
