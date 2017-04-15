package org.newstand.datamigration.net.protocol;

import com.google.common.primitives.Ints;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Next implements Serializable {

    private static final int SIZE = Ints.BYTES;

    private static final int CONTINUE = 0X99999;
    private static final int CANCEL = 0X66666;

    private Plans s;

    public Next(Plans S) {
        this.s = S;
    }

    public static Next continuing() {
        return new Next(Plans.CONTINUE);
    }

    public static Next cancel() {
        return new Next(Plans.CANCEL);
    }

    public static byte[] allocate() {
        return new byte[SIZE];
    }

    public static boolean isContinuing(byte[] data) {
        return Ints.fromByteArray(data) == CONTINUE;
    }

    public static void continuingTo(OutputStream os) throws IOException {
        os.write(continuing().toBytes());
    }

    public static void cancelTo(OutputStream os) throws IOException {
        os.write(cancel().toBytes());
    }

    public static void planTo(Plans plan, OutputStream os) throws IOException {
        switch (plan) {
            case CANCEL:
                cancelTo(os);
                break;
            case CONTINUE:
                continuingTo(os);
                break;
            default:
                throw new IllegalArgumentException("Bad plan #" + plan);
        }
    }

    @Override
    public byte[] toBytes() {
        return Ints.toByteArray(s == Plans.CONTINUE ? CONTINUE : CANCEL);
    }
}
