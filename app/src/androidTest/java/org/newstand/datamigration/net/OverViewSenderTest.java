package org.newstand.datamigration.net;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.OverviewHeader;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.net.server.TransportServerTest;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/22 17:24
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class OverViewSenderTest extends TransportServerTest {

    @Override
    @Test
    public void testServer() throws IOException {
        super.testServer();
    }

    @Override
    protected void write(TransportServer transportServer) {

        Collection<DataRecord> apps = DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App);

        OverViewSender sender = OverViewSender.with(transportServer.getInputStream(), transportServer.getOutputStream());

        try {
            int ret = sender.send(OverviewHeader.from(DataCategory.App, apps));
            Logger.d("Send ret %d", ret);
        } catch (IOException e) {
            Logger.e(Log.getStackTraceString(e));
            Assert.fail();
        }

    }

    @Override
    protected void read(TransportClient transportClient) {

        OverviewReceiver receiver = OverviewReceiver.with(transportClient.getInputStream(), transportClient.getOutputStream());
        try {
            int ret = receiver.receive(null);
            Logger.d("Receiver ret %d", ret);

            OverviewHeader header = receiver.getHeader();

            Logger.d(header);

        } catch (IOException e) {
            Logger.e(Log.getStackTraceString(e));
            Assert.fail();
        }


    }
}