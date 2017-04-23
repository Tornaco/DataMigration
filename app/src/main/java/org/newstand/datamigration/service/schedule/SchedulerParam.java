package org.newstand.datamigration.service.schedule;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/23 20:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@ToString
public class SchedulerParam {

    private Condition condition;
    private ScheduleAction action;

    public SchedulerParam(Condition condition, ScheduleAction action) {
        this.condition = condition;
        this.action = action;
    }
}
