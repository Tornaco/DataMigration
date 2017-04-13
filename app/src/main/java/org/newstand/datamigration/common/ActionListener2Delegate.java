package org.newstand.datamigration.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ActionListener2Delegate<RES, ERR> implements ActionListener2<RES, ERR> {

    private ActionListener2<RES, ERR> client;
    private Handler handler;

    public ActionListener2Delegate(ActionListener2<RES, ERR> client, Looper looper) {
        this.client = client;
        handler = new Handler(looper);
    }

    @Override
    public void onStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                client.onStart();
            }
        });
    }

    @Override
    public void onError(final ERR err) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                client.onError(err);
            }
        });
    }

    @Override
    public void onComplete(final RES res) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                client.onComplete(res);
            }
        });
    }
}
