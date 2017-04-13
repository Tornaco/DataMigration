package org.newstand.datamigration.common;

import android.os.Handler;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class ActionListener2MainThreadAdapter<RES, ERR> extends Handler implements ActionListener2<RES, ERR> {

    @Override
    public void onComplete(final RES res) {
        post(new Runnable() {
            @Override
            public void run() {
                onCompleteMainThread(res);
            }
        });
    }

    @Override
    public void onError(final ERR err) {
        post(new Runnable() {
            @Override
            public void run() {
                onErrorMainThread(err);
            }
        });
    }

    @Override
    public void onStart() {
        post(new Runnable() {
            @Override
            public void run() {
                onStartMainThread();
            }
        });
    }

    public abstract void onStartMainThread();

    public abstract void onErrorMainThread(ERR err);

    public abstract void onCompleteMainThread(RES res);
}
