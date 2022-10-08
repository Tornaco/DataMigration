package org.newstand.datamigration.net.server;

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/13 15:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class TransportServerProxyTest {
    @Test
    public void startWithPenitentialPortsAsync() throws Exception {
        TransportServerProxy.startWithPenitentialPortsAsync("localhost", SettingsProvider.getTransportServerPorts(),
                new TransportServer.ChannelHandler() {
                    @Override
                    public void onServerCreateFail(ErrorCode errCode) {
                        Assert.fail(errCode.toString());
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
                }, new Consumer<TransportServer>() {
                    @Override
                    public void accept(@NonNull TransportServer transportServer) {
                        Logger.d("applyImage %s", transportServer);
                    }
                });
    }

}