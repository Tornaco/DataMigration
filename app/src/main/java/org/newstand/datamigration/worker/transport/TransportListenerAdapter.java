package org.newstand.datamigration.worker.transport;

import org.newstand.datamigration.data.model.DataRecord;

/**
 * Created by Nick@NewStand.org on 2017/3/23 14:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportListenerAdapter extends TransportListener {
    @Override
    public void onStart() {
    }

    @Override
    public void onPieceStart(DataRecord record) {
    }

    @Override
    public void onPieceUpdate(DataRecord record, TransportEvent transportEvent, float pieceProgress) {

    }

    @Override
    public void onPieceSuccess(DataRecord record) {

    }

    @Override
    public void onPieceFail(DataRecord record, Throwable err) {
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onAbort(Throwable err) {

    }
}
