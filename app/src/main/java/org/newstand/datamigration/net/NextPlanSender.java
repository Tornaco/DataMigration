package org.newstand.datamigration.net;

import org.newstand.datamigration.net.protocol.Next;
import org.newstand.datamigration.net.protocol.Plans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/15 16:11
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
public class NextPlanSender extends AbsSender<Void> {

    private InputStream in;
    private OutputStream os;

    private Plans plan;

    private NextPlanSender(InputStream in, OutputStream os, Plans plan) {
        this.in = in;
        this.os = os;
        this.plan = plan;
    }

    public static NextPlanSender with(InputStream in, OutputStream os, Plans nextPlan) {
        return new NextPlanSender(in, os, nextPlan);
    }

    @Override
    public int send(Void v) throws IOException {
        Next.planTo(plan, getOs());
        return waitForAck(in);
    }
}
