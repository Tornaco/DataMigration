package org.newstand.datamigration.net;

import org.newstand.datamigration.net.protocol.ACK;
import org.newstand.datamigration.net.protocol.OverviewHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 17:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class OverviewReceiver implements Receiver<Void> {

    private InputStream inputStream;
    private OutputStream outputStream;

    private OverviewReceiver(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static OverviewReceiver with(InputStream inputStream, OutputStream outputStream) {
        return new OverviewReceiver(inputStream, outputStream);
    }

    @Setter
    @Getter
    OverviewHeader header;

    @Override
    public int receive(Void v) throws IOException {
        OverviewHeader header = OverviewHeader.from(inputStream);
        ACK.okTo(outputStream);
        setHeader(header);
        return OK;
    }
}
