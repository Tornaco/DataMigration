package org.newstand.datamigration.net;

import android.content.Context;
import android.os.PowerManager;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.utils.WakeLockWrapper;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/13 18:07
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportServer {

    static final String SERVER_URL = "http://127.0.0.1";
    static final int SERVER_PORT = 8899;


    @Getter
    private Context context;

    public TransportServer(Context context) {
        this.context = context;
    }

    void start() {
        PowerManager.WakeLock wakeLock = WakeLockWrapper.getWakeLockInstance(getContext(), getWorkerTag());
        wakeLock.acquire();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 配置服务器的NIO线程租
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(SERVER_PORT).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            Logger.d("server initChannel..");
            arg0.pipeline().addLast(new TimeServerHandler());
        }
    }

    class TimeServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            Logger.d("server channelRead..");
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body = new String(req, "UTF-8");
            Logger.d("The time server receive order:" + body);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
                    System.currentTimeMillis()).toString() : "BAD ORDER";
            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
            ctx.write(resp);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            Logger.d("server channelReadComplete..");
            ctx.flush();//刷新后才将数据发出到SocketChannel
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            Logger.d("server exceptionCaught..");
            ctx.close();
        }

    }

    public String getWorkerTag() {
        return getClass().getSimpleName();
    }
}
