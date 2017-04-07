package org.newstand.datamigration.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Nick@NewStand.org on 2017/4/7 13:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DummySmsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
