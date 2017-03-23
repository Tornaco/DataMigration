package org.newstand.datamigration.net;

import org.newstand.datamigration.net.protocol.OverviewHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 17:17
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class OverViewSender extends AbsSender<OverviewHeader> {

    private InputStream inputStream;
    private OutputStream outputStream;

    private OverViewSender(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static OverViewSender with(InputStream inputStream, OutputStream outputStream) {
        return new OverViewSender(inputStream, outputStream);
    }

    @Override
    public int send(OverviewHeader header) throws IOException {
        header.writeTo(outputStream);
        return waitForAck(inputStream);
    }
}
