package org.newstand.datamigration.net;

import android.os.PowerManager;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * When a network event is occurred, these methods will be called.
 * <p>
 * - 새로운 채널이 연결될 때 {@link #channelConnected(ChannelHandlerContext, ChannelStateEvent)}
 * <br/> - 채널에서 새로운 메시지가 유입될 때 {@link #messageReceived(ChannelHandlerContext, MessageEvent)}
 * <br/> - Exception 이 발생 했을 때 {@link #exceptionCaught(ChannelHandlerContext, ExceptionEvent)}
 * <br/> - 채널이 닫힐 때 {@link #channelClosed(ChannelHandlerContext, ChannelStateEvent)}
 * <br/>
 * <p>
 *
 * @author Hovan Yoo
 */
public class NetworkEventHandler extends SimpleChannelHandler {

    TransportServer mService;

    public NetworkEventHandler(TransportServer service) {
        mService = service;
    }

    /**
     * Session is connected!
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Logger.w(String.format("%s.channelConnected()", NetworkEventHandler.class.getSimpleName()));
        super.channelConnected(ctx, e);
    }

    /**
     * Some message was delivered
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        PowerManager.WakeLock wakeLock = WakeLockWrapper.getWakeLockInstance(mService.getContext(),
                NetworkEventHandler.class.getSimpleName());
        wakeLock.acquire();
        try {
            super.messageReceived(ctx, e);
            //You should do something with the received message.
        } finally {
            wakeLock.release();
        }
    }

    /**
     * An exception is occurred!
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Logger.w(String.format("%s.exceptionCaught() Exception: %s", NetworkEventHandler.class.getSimpleName(),
                Log.getStackTraceString(e.getCause())));
        super.exceptionCaught(ctx, e);

        if (ctx.getChannel() != null && ctx.getChannel().isOpen()) {
            ctx.getChannel().close();
        } else {
            // mService.scheduleToReconnect();
        }
    }

    /**
     * The channel is going to closed.
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Logger.w(String.format("%s.channelClosed()", NetworkEventHandler.class.getSimpleName()));
        super.channelClosed(ctx, e);
        // mService.scheduleToReconnect();
    }
}
