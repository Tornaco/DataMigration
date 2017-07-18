package org.newstand.datamigration.net.server;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.DataRecordReceiver;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.ReceiveSettings;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 15:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class TransportServerTest implements Consumer<Exception> {

    @Test
    public void testPortsInUse() {

        startServerWith(8888);
        startServerWith(9999);

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int[] ports = SettingsProvider.getTransportServerPorts();
                for (int port : ports) {
                    if (startServerWith(port)) {
                        Logger.d("Started @" + port);
                        return;
                    }
                }
                Assert.fail();
            }
        });

        Sleeper.sleepQuietly(20 * 1000);
    }

    private boolean startServerWith(int port) {

        String host = "localhost";

        final TransportServer transportServer = new TransportServer();
        transportServer.setChannelHandler(new TransportServer.ChannelHandler() {
            @Override
            public void onServerCreateFail(ErrorCode errCode) {
                Logger.d("Start fail:" + errCode);
            }

            @Override
            public void onServerChannelCreate() {
                Logger.d("onServerChannelCreate");
            }

            @Override
            public void onServerChannelStop() {

            }

            @Override
            public void onClientChannelCreated() {
                Logger.d("onClientChannelCreated");
            }
        });
        transportServer.setHost(host);
        transportServer.setPort(port);

        return transportServer.start();
    }


    @Test
    public void setChannelHandler() throws Exception {
        final TransportServer transportServer = new TransportServer();
        transportServer.setHost("127.0.0.1");
        transportServer.setPort(SettingsProvider.getTransportServerPorts()[0]);
        SharedExecutor.execute(transportServer.asRunnable(this));
        SharedExecutor.execute(transportServer.asRunnable(this));

        Sleeper.sleepQuietly(20 * 1000);
    }

    @Test
    public void testServer() throws IOException {

        final TransportServer transportServer = new TransportServer();
        transportServer.setHost("127.0.0.1");
        transportServer.setPort(9988);


        SharedExecutor.execute(transportServer.asRunnable(this));

        final TransportClient transportClient = new TransportClient();
        transportClient.setHost("127.0.0.1");
        transportClient.setPort(9988);

        transportClient.setChannelHandler(new TransportClient.ChannelHandler() {
            @Override
            public void onServerChannelConnected() {
                Logger.d("onServerChannelConnected");

                read(transportClient);
            }

            @Override
            public void onClientStop() {

            }

            @Override
            public void onServerChannelConnectedFailure(ErrorCode errCode) {

            }
        });

        transportServer.setChannelHandler(new TransportServer.ChannelHandler() {
            @Override
            public void onServerCreateFail(ErrorCode errCode) {

            }

            @Override
            public void onServerChannelCreate() {
                SharedExecutor.execute(transportClient.asRunnable(new Consumer<Exception>() {
                    @Override
                    public void accept(@NonNull Exception e) {
                        Assert.fail(e.getLocalizedMessage());
                    }
                }));
            }

            @Override
            public void onServerChannelStop() {

            }

            @Override
            public void onClientChannelCreated() {
                Logger.d("onClientChannelCreated");
                write(transportServer);
            }
        });

        Sleeper.sleepQuietly(1000 * 1000);
    }

    protected void write(TransportServer transportServer) {

        OutputStream outputStream = transportServer.getOutputStream();
        InputStream inputStream = transportServer.getInputStream();

        DataRecordSender sender = DataRecordSender.with(outputStream, inputStream, Session.create());//FIXMRE Use same session.

        sender.setContext(InstrumentationRegistry.getTargetContext());

        DataRecord r = DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.Contact)
                .iterator().next();

        Logger.d(r);

        try {
            int ret = sender.send(r);
            Logger.d("sender, Ret %d", ret);
        } catch (IOException e) {
            Assert.fail();
            e.printStackTrace();
        }
    }

    protected void read(TransportClient transportClient) {
        DataRecordReceiver recordReceiver = DataRecordReceiver.with(transportClient.getInputStream(), transportClient.getOutputStream());
        ReceiveSettings settings = new ReceiveSettings();
        String tmpPath = Environment.getExternalStorageDirectory().getPath();
        settings.setRootDir(tmpPath);

        try {
            int ret = recordReceiver.receive(settings);
            Logger.d("recordReceiver, Ret %d", ret);
        } catch (IOException e) {
            Assert.fail();
            e.printStackTrace();
        }
    }

    protected static DataRecord appRecord() {
        return DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App)
                .iterator().next();
    }

    @Override
    public void accept(@NonNull Exception e) {
        Assert.fail(e.getLocalizedMessage());
    }
}