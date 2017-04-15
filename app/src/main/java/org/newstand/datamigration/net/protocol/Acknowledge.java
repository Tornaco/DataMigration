package org.newstand.datamigration.net.protocol;

import com.google.common.primitives.Ints;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Acknowledge implements Serializable {

    private enum Quality {GOOD, BAD}

    private static final int SIZE = Ints.BYTES;

    private static final int GOOD = 0X1234567;
    private static final int BAD = 0x7654321;

    private Quality q;

    public Acknowledge(Quality q) {
        this.q = q;
    }

    public static Acknowledge ok() {
        return new Acknowledge(Quality.GOOD);
    }

    public static Acknowledge bad() {
        return new Acknowledge(Quality.BAD);
    }

    public static byte[] allocate() {
        return new byte[SIZE];
    }

    public static boolean isOk(byte[] data) {
        return Ints.fromByteArray(data) == GOOD;
    }

    public static void okTo(OutputStream os) throws IOException {
        os.write(ok().toBytes());
    }

    public static void badTo(OutputStream os) throws IOException {
        os.write(bad().toBytes());
    }

    @Override
    public byte[] toBytes() {
        return Ints.toByteArray(q == Quality.GOOD ? GOOD : BAD);
    }
}
