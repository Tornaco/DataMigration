package org.newstand.datamigration.service.schedule;

import org.newstand.datamigration.common.Producer;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ActionExecutorProducer extends Producer<ActionExecutor> {
    ActionSettings createEmptySettings();
}
