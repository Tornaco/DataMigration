package org.newstand.datamigration.utils;

import android.util.Log;

import com.orhanobut.logger.LogAdapter;

import org.newstand.datamigration.service.UserActionServiceProxy;

/**
 * Created by Nick@NewStand.org on 2017/3/24 15:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OnDeviceLogAdapter implements LogAdapter {

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
        UserActionServiceProxy.publishNewAction("D", tag + message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
        UserActionServiceProxy.publishNewAction("E", tag + message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
        UserActionServiceProxy.publishNewAction("W", tag + message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
        UserActionServiceProxy.publishNewAction("I", tag + message);
    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
        UserActionServiceProxy.publishNewAction("V", tag + message);
    }

    @Override
    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
        UserActionServiceProxy.publishNewAction("WTF", tag + message);
    }
}
