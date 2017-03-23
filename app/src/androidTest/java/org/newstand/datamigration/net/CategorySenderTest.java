package org.newstand.datamigration.net;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.CategoryHeader;
import org.newstand.datamigration.net.server.SocketClient;
import org.newstand.datamigration.net.server.SocketServer;
import org.newstand.datamigration.net.server.SocketServerTest;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/23 9:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class CategorySenderTest extends SocketServerTest {

    @Test
    public void testCategorySender() throws IOException {
        testServer();
    }

    @Override
    protected void read(SocketClient socketClient) {
        CategoryReceiver receiver = CategoryReceiver.with(socketClient.getInputStream(), socketClient.getOutputStream());
        try {
            receiver.receive(null);

            Logger.d("Rec %s", receiver.getHeader());

        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Override
    protected void write(SocketServer socketServer) {
        CategorySender sender = CategorySender.with(socketServer.getInputStream(), socketServer.getOutputStream());
        Collection<DataRecord> apps = DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App);
        try {
            CategoryHeader header = CategoryHeader.from(DataCategory.App).add(apps);
            Logger.d(header);
            sender.send(header);
        } catch (IOException e) {
            Assert.fail();
        }
    }
}