package org.newstand.datamigration.net;

import org.newstand.datamigration.net.protocol.Acknowledge;
import org.newstand.datamigration.net.protocol.Next;
import org.newstand.datamigration.net.protocol.Plans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/15 16:11
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
public class NextPlanReceiver implements Receiver<Void> {

    private InputStream inputStream;
    private OutputStream outputStream;

    private NextPlanReceiver(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public static NextPlanReceiver with(InputStream inputStream, OutputStream outputStream) {
        return new NextPlanReceiver(inputStream, outputStream);
    }

    @Setter
    private Plans plan;

    @Override
    public int receive(Void v) throws IOException {
        byte[] ack = Next.allocate();
        int ret = inputStream.read(ack);

        if (ret == -1) {
            return ERR_READ_PLAN;
        }

        Plans next = Next.isContinuing(ack) ? Plans.CONTINUE : Plans.CANCEL;

        setPlan(next);

        Acknowledge.okTo(outputStream);

        return IORES.OK;
    }
}
