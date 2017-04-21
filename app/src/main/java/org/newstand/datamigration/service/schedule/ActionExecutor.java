package org.newstand.datamigration.service.schedule;

/**
 * Created by Nick@NewStand.org on 2017/4/21 18:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ActionExecutor<PARAM> {
    int execute(PARAM param);
}
