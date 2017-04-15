package org.newstand.datamigration.worker.transport;

import org.newstand.datamigration.data.model.DataRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class TransportListener {

    @Setter
    @Getter
    private Stats stats;

    public abstract void onStart();

    public abstract void onPieceStart(DataRecord record);

    public abstract void onPieceSuccess(DataRecord record);

    public abstract void onPieceFail(DataRecord record, Throwable err);

    public abstract void onComplete();

    public abstract void onAbort(Throwable err);

}
