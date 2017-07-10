package org.newstand.datamigration.worker.transport.backup;

import lombok.Getter;

/**
 * Created by guohao4 on 2017/7/10.
 */

public class ProgressableBackupAgent<B extends BackupSettings, R extends RestoreSettings> implements BackupAgent<B, R> {

    @Getter
    private ProgressListener progressListener;

    @Override
    public void listen(ProgressListener listener) {
        this.progressListener = listener;
    }

    @Override
    public Res backup(B backupSettings) throws Exception {
        return null;
    }

    @Override
    public Res restore(R restoreSettings) throws Exception {
        return null;
    }
}
