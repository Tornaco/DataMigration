package org.newstand.datamigration.net;

import android.content.Context;
import android.os.PowerManager;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/13 18:07
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportServer {

    static boolean isConnectAlreadyScheduled = false;
    static final String SERVER_URL = "http://localhost";
    static final int SERVER_PORT = 8080;

    Channel mChannel;

    @Getter
    private Context context;

    public TransportServer(Context context) {
        this.context = context;
    }

    void startServer() {
        PowerManager.WakeLock wakeLock = WakeLockWrapper.getWakeLockInstance(getContext(), getWorkerTag());
        wakeLock.acquire();

        try {
            NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

            ClientBootstrap bootstrap = new ClientBootstrap(factory);

            // Set up the pipeline factory.
            bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                public ChannelPipeline getPipeline() throws Exception {
                    return Channels.pipeline(
                            new ChannelDecoder(getContext()),
                            new NetworkEventHandler(TransportServer.this),
                            new ChannelEncoder(getContext()));
                }
            });

            // Bind and start to accept incoming connections.
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(SERVER_URL, SERVER_PORT));
            future.awaitUninterruptibly();
            mChannel = future.getChannel();
        } finally {
            wakeLock.release();
        }
    }

    public String getWorkerTag() {
        return getClass().getSimpleName();
    }
}
