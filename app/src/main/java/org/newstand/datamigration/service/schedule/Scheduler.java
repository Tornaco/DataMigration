package org.newstand.datamigration.service.schedule;

/**
 * Created by Nick@NewStand.org on 2017/4/7 17:10
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface Scheduler {
    void watch(ServiceWatcher watcher);

    void schedule(Condition condition, ScheduleAction action);

    void unWatch(ServiceWatcher watcher);
}