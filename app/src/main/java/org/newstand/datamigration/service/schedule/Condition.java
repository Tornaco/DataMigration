package org.newstand.datamigration.service.schedule;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@ToString
public class Condition {
    // Requirements.
    private boolean requiresCharging;
    private boolean requiresDeviceIdle;
    private int networkType;
    private boolean isPersisted;
}
