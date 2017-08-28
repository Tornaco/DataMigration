package org.newstand.datamigration.worker.transport;

import android.os.Handler;
import android.os.Message;

import org.newstand.datamigration.data.model.DataRecord;


/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportListenerMainThreadAdapter extends TransportListener implements Handler.Callback {

    private static final int MSG_PROGRESS_UPDATE = 0X100;

    private Handler handler;

    public TransportListenerMainThreadAdapter() {
        handler = new Handler(this);
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
    public final void onRecordStart(final DataRecord record) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onRecordStartMainThread(record);
            }
        });
    }

    @Override
    public final void onRecordProgressUpdate(final DataRecord record, final RecordEvent recordEvent,
                                             final float progress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onRecordProgressUpdateMainThread(record, recordEvent, progress);
            }
        });
    }

    @Override
    public final void onRecordSuccess(final DataRecord record) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onRecordSuccessMainThread(record);
            }
        });
    }

    @Override
    public final void onRecordFail(final DataRecord record, final Throwable err) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onRecordFailMainThread(record, err);
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
    public final void onProgressUpdate(final float progress) {
        handler.obtainMessage(MSG_PROGRESS_UPDATE, progress).sendToTarget();
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

    public void onRecordStartMainThread(DataRecord record) {
    }

    public void onRecordProgressUpdateMainThread(DataRecord record, RecordEvent recordEvent, float progress) {

    }

    public void onRecordSuccessMainThread(DataRecord record) {
    }

    public void onRecordFailMainThread(DataRecord record, Throwable err) {
    }

    public void onProgressUpdateMainThread(float progress) {
    }

    public void onCompleteMainThread() {
    }

    public void onAbortMainThread(Throwable err) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_PROGRESS_UPDATE:
                onProgressUpdateMainThread((Float) msg.obj);
                return true;
        }
        return false;
    }
}
