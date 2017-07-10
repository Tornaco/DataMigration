package org.newstand.datamigration.worker.transport.backup;

import org.newstand.datamigration.worker.transport.TransportEvent;

/**
 * Interface definition for a callback to be invoked regularly as
 * verification proceeds.
 */
public interface ProgressListener {
    /**
     * Called periodically as the verification progresses.
     *
     * @param progress the approximate percentage of the
     *                 verification that has been completed, ranging from 0
     *                 to 100 (inclusive).
     */
    public void onProgress(TransportEvent event, float progress);
}