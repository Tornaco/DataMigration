package org.newstand.datamigration.io;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ByteSink {
    boolean accept(byte[] data);
}
