package org.newstand.datamigration.strategy;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum Interval {

    Seconds(T.INTERVAL_SECOND_MILLS),
    Minutes(T.INTERVAL_MINUTE_MILLS),
    Hour(T.INTERVAL_HOUR_MILLS),
    Day(T.INTERVAL_DAY_MILLS),
    Week(T.INTERVAL_WEEK_MILLS);

    @Getter
    private long intervalMills;

    Interval(long intervalMills) {
        this.intervalMills = intervalMills;
    }

    private class T {
        private static final long INTERVAL_SECOND_MILLS = 1000;
        private static final long INTERVAL_MINUTE_MILLS = 60 * 1000;
        private static final long INTERVAL_HOUR_MILLS = 60 * INTERVAL_MINUTE_MILLS;
        private static final long INTERVAL_DAY_MILLS = 24 * INTERVAL_HOUR_MILLS;
        private static final long INTERVAL_WEEK_MILLS = 7 * INTERVAL_DAY_MILLS;
    }
}
