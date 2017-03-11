package org.newstand.datamigration.worker.backup;

import org.newstand.datamigration.model.DataRecord;

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
    private Status status;

    public abstract void onStart();

    public abstract void onPieceStart(DataRecord record);

    public abstract void onPieceSuccess(DataRecord record);

    public abstract void onPieceFail(DataRecord record, Throwable err);

    public abstract void onComplete();

    public interface Status {
        int getTotal();

        int getLeft();

        int getSuccess();

        int getFail();
    }
}
