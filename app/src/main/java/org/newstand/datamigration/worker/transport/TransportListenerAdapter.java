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
    public void onEvent(Event event) {

    }

    @Override
    public void onRecordStart(DataRecord record) {
    }

    @Override
    public void onRecordProgressUpdate(DataRecord record, RecordEvent recordEvent, float progress) {

    }

    @Override
    public void onRecordSuccess(DataRecord record) {

    }

    @Override
    public void onRecordFail(DataRecord record, Throwable err) {
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onProgressUpdate(float progress) {

    }

    @Override
    public void onAbort(Throwable err) {

    }
}
