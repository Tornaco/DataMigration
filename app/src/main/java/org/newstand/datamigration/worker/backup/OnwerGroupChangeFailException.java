package org.newstand.datamigration.worker.backup;

/**
 * Created by Nick@NewStand.org on 2017/4/6 13:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OnwerGroupChangeFailException extends BackupAgent.Res {
    public OnwerGroupChangeFailException() {
    }

    public OnwerGroupChangeFailException(String message) {
        super(message);
    }

    public OnwerGroupChangeFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnwerGroupChangeFailException(Throwable cause) {
        super(cause);
    }
}
