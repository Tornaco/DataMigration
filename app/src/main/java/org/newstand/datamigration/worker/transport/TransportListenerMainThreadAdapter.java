package org.newstand.datamigration.worker.transport;

import android.os.Handler;

import org.newstand.datamigration.data.model.DataRecord;


/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportListenerMainThreadAdapter extends TransportListener {

    private Handler handler;

    public TransportListenerMainThreadAdapter() {
        handler = new Handler();
    }

    @Override
    public final void onStart() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onStartMainThread();
            }
        });
    }

    @Override
    public final void onPieceStart(final DataRecord record) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPieceStartMainThread(record);
            }
        });
    }

    @Override
    public void onPieceUpdate(final DataRecord record, final ChildEvent childEvent,
                              final float pieceProgress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPieceUpdateMainThread(record, childEvent, pieceProgress);
            }
        });
    }

    @Override
    public final void onPieceSuccess(final DataRecord record) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPieceSuccessMainThread(record);
            }
        });
    }

    @Override
    public final void onPieceFail(final DataRecord record, final Throwable err) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPieceFailMainThread(record, err);
            }
        });
    }

    @Override
    public final void onComplete() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onCompleteMainThread();
            }
        });
    }

    @Override
    public final void onAbort(final Throwable err) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onAbortMainThread(err);
            }
        });
    }

    public void onStartMainThread() {
    }

    public void onPieceStartMainThread(DataRecord record) {
    }

    public void onPieceUpdateMainThread(DataRecord record, ChildEvent childEvent, float pieceProgress) {

    }

    public void onPieceSuccessMainThread(DataRecord record) {
    }

    public void onPieceFailMainThread(DataRecord record, Throwable err) {
    }

    public void onCompleteMainThread() {
    }

    public void onAbortMainThread(Throwable err) {

    }
}
