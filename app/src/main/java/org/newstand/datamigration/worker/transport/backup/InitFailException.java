package org.newstand.datamigration.worker.transport.backup;

/**
 * Created by Nick@NewStand.org on 2017/4/6 10:12
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class InitFailException extends BackupAgent.Res {
    public InitFailException() {
    }

    public InitFailException(String message) {
        super(message);
    }

    public InitFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitFailException(Throwable cause) {
        super(cause);
    }
}
