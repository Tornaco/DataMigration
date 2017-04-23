package org.newstand.datamigration.service.schedule;

/**
 * Created by Nick@NewStand.org on 2017/4/22 10:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ServiceWatcher {
    void onTaskAdd(Condition condition, ScheduleAction action);

    void onTaskScheduled(Condition condition, ScheduleAction action);
}
