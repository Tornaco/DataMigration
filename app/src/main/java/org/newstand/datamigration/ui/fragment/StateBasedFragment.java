package org.newstand.datamigration.ui.fragment;

import android.os.Handler;
import android.os.Looper;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/28 17:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class StateBasedFragment extends TransitionSafeFragment {

    @Getter
    private Handler handler = new Handler(Looper.getMainLooper());

    protected static final int STATE_UNINITIALIZED = -0X100;

    @Getter
    private int state = STATE_UNINITIALIZED;

    public void enterState(final int state) {
        this.state = state;
        post(new Runnable() {
            @Override
            public void run() {
                handleState(state);
            }
        });
    }

    protected void post(Runnable r) {
        handler.post(r);
    }

    abstract void handleState(int state);
}
