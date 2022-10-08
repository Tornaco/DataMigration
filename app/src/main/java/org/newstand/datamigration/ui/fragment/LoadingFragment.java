package org.newstand.datamigration.ui.fragment;

import androidx.annotation.UiThread;

/**
 * Created by Nick@NewStand.org on 2017/3/30 10:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class LoadingFragment<DATA> extends StateBasedFragment {

    final int STATE_LOADING_REQUEST_START = 0x1231;
    final int STATE_LOADING_COMPLETE = 0x1232;

    void requestLoading() {
        enterState(STATE_LOADING_REQUEST_START);
    }

    void loadingComplete(DATA data) {
        enterState(STATE_LOADING_COMPLETE, data);
    }

    abstract void onRequestLoading();

    abstract void onLoadingComplete(DATA data);

    @SuppressWarnings("unchecked")
    @UiThread
    @Override
    void handleState(int state, Object obj) {
        switch (state) {
            case STATE_LOADING_REQUEST_START:
                onRequestLoading();
                break;
            case STATE_LOADING_COMPLETE:
                onLoadingComplete((DATA) obj);
                break;
        }
    }
}
