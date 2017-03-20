package org.newstand.datamigration.worker.backup;

import android.os.Handler;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.model.DataRecord;


/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupRestoreListenerMainThreadAdapter extends BackupRestoreListener {

    private Handler handler;

    public BackupRestoreListenerMainThreadAdapter() {
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

    public void onStartMainThread() {
        Logger.d("onStartMainThread:" + getStatus().getTotal());
    }

    public void onPieceStartMainThread(DataRecord record) {
        Logger.d("onPieceStartMainThread:%s", record.getDisplayName());
    }

    public void onPieceSuccessMainThread(DataRecord record) {
        Logger.d("onPieceSuccessMainThread:%s", record.getDisplayName());
    }

    public void onPieceFailMainThread(DataRecord record, Throwable err) {
        Logger.d("onPieceFailMainThread:%s, %s", record.getDisplayName(), Log.getStackTraceString(err));
    }

    public void onCompleteMainThread() {
        Logger.d("onCompleteMainThread");
    }
}
