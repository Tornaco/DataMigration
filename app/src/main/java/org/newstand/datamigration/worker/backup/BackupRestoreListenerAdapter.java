package org.newstand.datamigration.worker.backup;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.model.DataRecord;

/**
 * Created by Nick@NewStand.org on 2017/3/23 14:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupRestoreListenerAdapter extends BackupRestoreListener {
    @Override
    public void onStart() {
    }

    @Override
    public void onPieceStart(DataRecord record) {
    }

    @Override
    public void onPieceSuccess(DataRecord record) {

    }

    @Override
    public void onPieceFail(DataRecord record, Throwable err) {
        Logger.e("onPieceFail %s %s", record, err.getLocalizedMessage());
    }

    @Override
    public void onComplete() {

    }
}
