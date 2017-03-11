package org.newstand.datamigration.worker.backup;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface BackupAgent<B extends BackupSettings, R extends RestoreSettings> {
    void backup(B backupSettings) throws Exception;

    void restore(R restoreSettings) throws Exception;
}
