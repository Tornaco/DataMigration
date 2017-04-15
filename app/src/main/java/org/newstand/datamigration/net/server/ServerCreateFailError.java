package org.newstand.datamigration.net.server;

/**
 * Created by Nick@NewStand.org on 2017/4/15 14:52
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ServerCreateFailError extends Error {
    public ServerCreateFailError(ErrorCode errorCode) {
        super("Error code:" + errorCode);
    }
}
