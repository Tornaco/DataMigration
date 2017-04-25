package org.newstand.datamigration.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.newstand.datamigration.service.schedule.SchedulerServiceProxy;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/25 10:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("On boot complete, starting Scheduler.");
        SchedulerServiceProxy.start(context);
    }
}
