package org.newstand.datamigration.common;

/**
 * Created by Nick@NewStand.org on 2017/3/10 15:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AbortException extends Exception {
    public AbortException() {
    }

    public AbortException(String message) {
        super(message);
    }
}
