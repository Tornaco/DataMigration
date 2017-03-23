package org.newstand.datamigration.net.protocol;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 10:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public interface ByteWriter {
    void writeTo(OutputStream os) throws IOException;
}
