package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.worker.transport.RecordEvent;

/**
 * Interface definition for a callback to be invoked regularly as
 * verification proceeds.
 */
public interface ProgressListener {

    int NONE_PROGRESS = -1;

    /**
     * Called periodically as the verification progresses.
     *
     * @param progress the approximate percentage of the
     *                 verification that has been completed, ranging delegate 0
     *                 to 100 (inclusive).
     */
    public void onProgress(RecordEvent event, float progress);
}