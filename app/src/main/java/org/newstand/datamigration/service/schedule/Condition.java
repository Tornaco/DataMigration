package org.newstand.datamigration.service.schedule;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@Setter
@ToString
public class Condition {

    public static Condition DEFAULT =
            Condition.builder()
                    .triggerAtMills(System.currentTimeMillis())
                    .networkType(NetworkType.NETWORK_TYPE_ANY.ordinal())
                    .triggerContentUris(null)
                    .requiresDeviceIdle(false)
                    .isPersisted(true)
                    .repeat(true)
                    .requiresCharging(true)
                    .build();

    // Requirements.
    private boolean requiresCharging;
    @RequiresApi(Build.VERSION_CODES.M)
    private boolean requiresDeviceIdle;
    private int networkType;
    private boolean isPersisted;
    private long triggerAtMills;
    private List<String> triggerContentUris;
    private boolean repeat;
}
