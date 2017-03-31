package org.newstand.datamigration.net.server;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nick@NewStand.org on 2017/3/22 15:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SocketServerTest implements Consumer<Exception> {

    @Test
    public void testPortsInUse() {
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
    }

    private boolean startServerWith(int port) {

        String host = "localhost";

        final SocketServer socketServer = new SocketServer();
        socketServer.setChannelHandler(new SocketServer.ChannelHandler() {
            @Override
            public void onServerCreateFail(int errCode) {

            }

            @Override
            public void onServerChannelCreate() {
                Sleeper.sleepQuietly(2 * 1000);
                socketServer.stop();
            }

            @Override
            public void onClientChannelCreated() {

            }
        });
        socketServer.setHost(host);
        socketServer.setPort(port);

        return socketServer.start();
    }


    @Test
    public void setChannelHandler() throws Exception {
        final SocketServer socketServer = new SocketServer();
        socketServer.setHost("127.0.0.1");
        socketServer.setPort(SettingsProvider.getTransportServerPorts()[0]);
        SharedExecutor.execute(socketServer.asRunnable(this));
        SharedExecutor.execute(socketServer.asRunnable(this));

        Sleeper.sleepQuietly(20 * 1000);
    }

    @Test
    public void testServer() throws IOException {

        final SocketServer socketServer = new SocketServer();
        socketServer.setHost("127.0.0.1");
        socketServer.setPort(9988);


        SharedExecutor.execute(socketServer.asRunnable(this));

        final SocketClient socketClient = new SocketClient();
        socketClient.setHost("127.0.0.1");
        socketClient.setPort(9988);

        socketClient.setChannelHandler(new SocketClient.ChannelHandler() {
            @Override
            public void onServerChannelConnected() {
                Logger.d("onServerChannelConnected");

                read(socketClient);
            }
        });

        socketServer.setChannelHandler(new SocketServer.ChannelHandler() {
            @Override
            public void onServerCreateFail(int errCode) {

            }

            @Override
            public void onServerChannelCreate() {
                SharedExecutor.execute(socketClient.asRunnable(new Consumer<Exception>() {
                    @Override
                    public void consume(@NonNull Exception e) {
                        Assert.fail(e.getLocalizedMessage());
                    }
                }));
            }

            @Override
            public void onClientChannelCreated() {
                Logger.d("onClientChannelCreated");
                write(socketServer);
            }
        });

        Sleeper.sleepQuietly(1000 * 1000);
    }

    protected void write(SocketServer socketServer) {

        OutputStream outputStream = socketServer.getOutputStream();
        InputStream inputStream = socketServer.getInputStream();

        DataRecordSender sender = DataRecordSender.with(outputStream, inputStream);

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

    protected void read(SocketClient socketClient) {
        DataRecordReceiver recordReceiver = DataRecordReceiver.with(socketClient.getInputStream(), socketClient.getOutputStream());
        ReceiveSettings settings = new ReceiveSettings();
        String tmpPath = Environment.getExternalStorageDirectory().getPath();
        settings.setDestDir(tmpPath);

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
    public void consume(@NonNull Exception e) {
        Assert.fail(e.getLocalizedMessage());
    }
}