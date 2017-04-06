package org.newstand.datamigration.worker.backup;

/**
 * Created by Nick@NewStand.org on 2017/4/6 12:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class MissingUtilException extends BackupAgent.Res {
    public MissingUtilException(String message) {
        super(message);
    }
}
