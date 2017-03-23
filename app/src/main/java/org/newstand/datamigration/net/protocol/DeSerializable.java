package org.newstand.datamigration.net.protocol;

/**
 * Created by Nick@NewStand.org on 2017/3/21 14:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface DeSerializable {
    void inflateWithBytes(byte[] data);
}
