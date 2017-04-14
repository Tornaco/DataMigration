package org.newstand.datamigration.net.protocol;

import com.google.common.primitives.Ints;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/4/14 10:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class EODHeader implements Serializable, DeSerializable, ByteWriter {

    private static final int END_OF_DATA = 0X88998899;

    private int i;

    public boolean isEnd() {
        return i == END_OF_DATA;
    }

    @Override
    public void inflateWithBytes(byte[] data) {
        i = Ints.fromByteArray(data);
    }

    @Override
    public byte[] toBytes() {
        return Ints.toByteArray(END_OF_DATA);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(toBytes());
    }
}
