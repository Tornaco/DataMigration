package org.newstand.datamigration.common;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;


/**
 * Created by Nick@NewStand.org on 2017/3/10 9:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class ActionListenerMainThreadAdapter<T> extends Handler implements ActionListener<T> {

    public ActionListenerMainThreadAdapter(Looper looper) {
        super(looper);
    }

    @Override
    public final void onAction(@Nullable final T t) {
        post(new Runnable() {
            @Override
            public void run() {
                onActionMainThread(t);
            }
        });
    }


    public abstract void onActionMainThread(@Nullable T t);
}
