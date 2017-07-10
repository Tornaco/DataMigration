package org.newstand.datamigration.worker.transport.backup;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:19
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface BackupAgent<B extends BackupSettings, R extends RestoreSettings> {

    void listen(ProgressListener listener);

    Res backup(B backupSettings) throws Exception;

    Res restore(R restoreSettings) throws Exception;

    class Res extends Exception {

        public static final Res OK = null;

        public Res() {
        }

        public Res(String message) {
            super(message);
        }

        public Res(String message, Throwable cause) {
            super(message, cause);
        }

        public Res(Throwable cause) {
            super(cause);
        }

        public static boolean isOk(Res r) {
            return Res.OK == r;
        }
    }
}
