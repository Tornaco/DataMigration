package org.newstand.datamigration.net.server;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/4/13 15:44
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class TransportClientProxyTest {
    @Test
    public void startWithPenitentialPortsAsync() throws Exception {
        TransportClientProxy.startWithPenitentialPortsAsync("localhost", SettingsProvider.getTransportServerPorts(),
                new TransportClient.ChannelHandler() {
                    @Override
                    public void onServerChannelConnected() {

                    }

                    @Override
                    public void onClientStop() {

                    }

                    @Override
                    public void onServerChannelConnectedFailure(ErrorCode errCode) {

                    }
                }, new Consumer<TransportClient>() {
                    @Override
                    public void accept(@NonNull TransportClient transportClient) {

                    }
                });
    }

}