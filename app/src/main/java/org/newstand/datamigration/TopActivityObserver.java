package org.newstand.datamigration;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.io.Closeable;
import java.io.IOException;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/16 11:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TopActivityObserver implements Application.ActivityLifecycleCallbacks, Closeable {

    @Getter
    private Activity topActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        topActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void close() throws IOException {
        topActivity = null;
    }
}
