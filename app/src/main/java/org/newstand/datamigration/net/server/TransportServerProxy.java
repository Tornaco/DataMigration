package org.newstand.datamigration.net.server;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.sync.SharedExecutor;

/**
 * Created by Nick@NewStand.org on 2017/4/13 13:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class TransportServerProxy {

    public static void startWithPenitentialPortsAsync(final String host, final int[] ports,
                                                      final TransportServer.ChannelHandler channelHandler,
                                                      final Consumer<TransportServer> transportServerConsumer) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int port : ports) {
                    if (startServerWith(host, port, channelHandler, transportServerConsumer)) {
                        return;
                    }
                }
            }
        });
    }

    private static boolean startServerWith(String host, int port, TransportServer.ChannelHandler channelHandler,
                                           Consumer<TransportServer> transportServerConsumer) {

        TransportServer transportServer = new TransportServer();
        transportServer.setChannelHandler(channelHandler);
        transportServer.setHost(host);
        transportServer.setPort(port);

        transportServerConsumer.accept(transportServer);

        return transportServer.start();
    }

}
