package org.newstand.datamigration.utils;

import com.google.common.primitives.Ints;

/**
 * Created by Nick@NewStand.org on 2017/3/21 14:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class DataConverter {

    public static byte[] intToBytes(int value) {
        return Ints.toByteArray(value);
    }

    public static int bytesToInt(byte[] bytes) {
        return Ints.fromByteArray(bytes);
    }
}
