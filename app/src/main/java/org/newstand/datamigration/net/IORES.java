package org.newstand.datamigration.net;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:44
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface IORES {
    int OK = 0;
    int ERR_READ_ACK = 0x1;
    int ERR_BAD_ACK = 0x2;

    int ERR_WRITE_FAIL_IN_OUT_SIZE_MISMATCH = 0x3;

    int ERR_READ_PLAN = 0x4;
    int ERR_BAD_PLAN = 0x5;
}
