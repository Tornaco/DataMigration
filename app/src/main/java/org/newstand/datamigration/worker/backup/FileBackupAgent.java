package org.newstand.datamigration.worker.backup;

import com.google.common.io.Files;

import org.newstand.datamigration.utils.BlackHole;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class FileBackupAgent implements BackupAgent<FileBackupSettings, FileRestoreSettings> {

    @Override
    public Res backup(FileBackupSettings backupSettings) throws Exception {
        String from = backupSettings.getSourcePath();
        String to = backupSettings.getDestPath();
        return copy(from, to);
    }

    @Override
    public Res restore(FileRestoreSettings restoreSettings) throws Exception {
        String from = restoreSettings.getSourcePath();
        String to = restoreSettings.getDestPath();
        return copy(from, to);
    }

    private Res copy(String from, String to) throws Exception {
        BlackHole.eat(new File(to).delete());
        Files.createParentDirs(new File(to));
        Files.copy(new File(from), new File(to));
        return Res.OK;
    }
}
