package org.newstand.datamigration.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Nick@NewStand.org on 2017/4/15 23:13
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DummSmsServiceProxy extends ServiceProxy {

    private DummSmsServiceProxy(Context context, Intent intent) {
        super(context, intent);
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, DummySmsService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, DummySmsService.class));
    }

    @Override
    public void onConnected(IBinder binder) {

    }
}
