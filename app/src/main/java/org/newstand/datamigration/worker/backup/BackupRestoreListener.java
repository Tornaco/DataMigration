package org.newstand.datamigration.worker.backup;

import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.worker.Stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class BackupRestoreListener {

    @Setter(AccessLevel.PACKAGE)
    @Getter
    private Stats stats;

    public abstract void onStart();

    public abstract void onPieceStart(DataRecord record);

    public abstract void onPieceSuccess(DataRecord record);

    public abstract void onPieceFail(DataRecord record, Throwable err);

    public abstract void onComplete();

}
