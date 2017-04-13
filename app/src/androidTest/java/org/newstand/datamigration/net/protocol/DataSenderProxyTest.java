package org.newstand.datamigration.net.protocol;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.net.server.TransportClientProxy;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.net.server.TransportServerProxy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Nick@NewStand.org on 2017/4/13 13:25
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class DataSenderProxyTest {

    private TransportServer mServer;
    private TransportClient mClient;

    private CountDownLatch mWaiter;

    private void mokeServerThenClient() {
        TransportServerProxy.startWithPenitentialPortsAsync("localhost", SettingsProvider.getTransportServerPorts(),
                new TransportServer.ChannelHandlerAdapter() {
                    @Override
                    public void onServerCreateFail(ErrorCode errCode) {
                        Assert.fail(errCode.toString());
                    }

                    @Override
                    public void onServerChannelCreate() {
                        mokeClient();
                    }

                    @Override
                    public void onClientChannelCreated() {
                        Logger.d("onClientChannelCreated");
                    }
                }, new Consumer<TransportServer>() {
                    @Override
                    public void accept(@NonNull TransportServer transportServer) {
                        mServer = transportServer;
                        mWaiter.countDown();
                    }
                });

    }

    private void mokeClient() {
        TransportClientProxy.startWithPenitentialPortsAsync("localhost", SettingsProvider.getTransportServerPorts(),
                new TransportClient.ChannelHandler() {
                    @Override
                    public void onServerChannelConnected() {
                        Logger.d("onServerChannelConnected");
                        dump();
                    }

                    @Override
                    public void onClientStop() {

                    }

                    @Override
                    public void onServerChannelConnectedFailure(ErrorCode errCode) {
                        Assert.fail(errCode.toString());
                    }
                }, new Consumer<TransportClient>() {
                    @Override
                    public void accept(@NonNull TransportClient transportClient) {
                        mClient = transportClient;
                    }
                });
    }

    private void dump() {
        Logger.d("Server %s", mServer);
        Logger.d("Client %s", mClient);
    }

    private void waitForServerAndClientReady() throws InterruptedException {
        mWaiter = new CountDownLatch(1);
        mWaiter.await();
    }

    @Test
    public void send() throws Exception {
        mokeServerThenClient();
        waitForServerAndClientReady();
    }

}