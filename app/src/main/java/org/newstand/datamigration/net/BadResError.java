package org.newstand.datamigration.net;

/**
 * Created by Nick@NewStand.org on 2017/4/15 13:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BadResError extends Error {
    public BadResError(int res) {
        super("Bad res:" + res);
    }
}
