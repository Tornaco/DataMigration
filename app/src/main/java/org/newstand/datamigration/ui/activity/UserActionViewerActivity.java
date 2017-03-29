package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.service.UserActionServiceProxy;
import org.newstand.datamigration.sync.Threads;
import org.newstand.datamigration.utils.Collections;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionViewerActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showHomeAsUp();

        Threads.started(new Runnable() {
            @Override
            public void run() {
                Collections.consumeRemaining(UserActionServiceProxy.getAll(getApplicationContext()), new Consumer<UserAction>() {
                    @Override
                    public void consume(@NonNull UserAction action) {
                        Logger.d(action);
                    }
                });
            }
        });
    }
}
