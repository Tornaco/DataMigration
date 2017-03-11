package org.newstand.datamigration.loader;

import android.os.Handler;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/7 11:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LoaderListenerMainThreadAdapter<T> implements LoaderListener<T> {

    private Handler mHandler;

    public LoaderListenerMainThreadAdapter() {
        mHandler = new Handler();
    }

    @Override
    public final void onStart() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onStartMainThread();
            }
        });
    }

    @Override
    public final void onComplete(final Collection<T> collection) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteMainThread(collection);
            }
        });
    }

    @Override
    public final void onErr(final Throwable throwable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onErrMainThread(throwable);
            }
        });
    }

    public void onStartMainThread() {

    }


    public void onCompleteMainThread(Collection<T> collection) {

    }

    public void onErrMainThread(Throwable throwable) {

    }
}
