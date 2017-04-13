package org.newstand.datamigration.net.server;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.sync.SharedExecutor;

/**
 * Created by Nick@NewStand.org on 2017/4/13 15:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class TransportClientProxy {

    public static void startWithPenitentialPortsAsync(final String host, final int[] ports,
                                                      final TransportClient.ChannelHandler channelHandler,
                                                      final Consumer<TransportClient> transportServerConsumer) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (int port : ports) {
                    if (startClientWith(host, port, channelHandler, transportServerConsumer)) {
                        return;
                    }
                }
            }
        });
    }

    private static boolean startClientWith(String host, int port, TransportClient.ChannelHandler channelHandler,
                                           Consumer<TransportClient> transportServerConsumer) {

        final TransportClient client = new TransportClient();
        client.setHost(host);
        client.setPort(port);

        client.setChannelHandler(channelHandler);

        transportServerConsumer.accept(client);

        return client.start();
    }
}
