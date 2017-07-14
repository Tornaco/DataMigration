package org.newstand.datamigration;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;

import java.io.Closeable;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/16 11:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class TopActivityObserver implements Application.ActivityLifecycleCallbacks, Closeable {

    @Getter
    private Activity topActivity;

    @Setter
    private Consumer<Activity> onMainActivityDestroyConsumer;
    @Setter
    private Consumer<Activity> onMainActivityStartConsumer;

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        if (activity instanceof TransitionSafeActivity) {
            TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) activity;
            if (transitionSafeActivity.isMainActivity()) {
                onMainActivityStartConsumer.accept(activity);
            }
        }
    }

    @Override
    public void onActivityStarted(final Activity activity) {
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
        if (activity instanceof TransitionSafeActivity) {
            TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) activity;
            if (transitionSafeActivity.isMainActivity()) {
                onMainActivityDestroyConsumer.accept(activity);
            }
        }
    }

    @Override
    public void close() throws IOException {
        topActivity = null;
    }
}
