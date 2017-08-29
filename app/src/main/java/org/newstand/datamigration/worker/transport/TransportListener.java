package org.newstand.datamigration.worker.transport;

import org.newstand.datamigration.data.model.DataRecord;

/**
 * Created by Nick@NewStand.org on 2017/3/9 10:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class TransportListener {

    public abstract void onStart();

    public abstract void onEvent(Event event);

    public abstract void onRecordStart(DataRecord record);

    public abstract void onRecordProgressUpdate(DataRecord record, RecordEvent recordEvent, float progress);

    public abstract void onRecordSuccess(DataRecord record);

    public abstract void onRecordFail(DataRecord record, Throwable err);

    public abstract void onComplete();

    public abstract void onProgressUpdate(float progress);

    public abstract void onAbort(Throwable err);

}
