package org.newstand.datamigration.net.server;

import com.google.common.io.Closer;

import org.newstand.logger.Logger;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 15:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransportClient extends ServerComponent {

    @Getter
    private Closer closer = Closer.create();

    @Getter
    @Setter
    private ChannelHandler channelHandler;

    @Override
    public boolean start() {

        Logger.d("Starting client %s", toString());

        try {
            Socket socket = closer.register(new Socket(getHost(), getPort()));

            setOutputStream(closer.register(socket.getOutputStream()));
            setInputStream(closer.register(socket.getInputStream()));

        } catch (IOException e) {
            channelHandler.onServerChannelConnectedFailure(ErrorCode.ERROR_UNKNOWN);
            stop();
            return false;
        }
        if (channelHandler != null) channelHandler.onServerChannelConnected();

        return true;
    }

    @Override
    public boolean stop() {
        try {
            closer.close();
            channelHandler.onClientStop();
        } catch (IOException e) {
            Logger.e(e, "Close fail");
            return false;
        }
        return true;
    }

    public interface ChannelHandler {

        void onServerChannelConnected();

        void onClientStop();

        void onServerChannelConnectedFailure(ErrorCode errCode);
    }

    public static class ChannelHandlerAdapter implements ChannelHandler {

        @Override
        public void onServerChannelConnected() {

        }

        @Override
        public void onClientStop() {

        }

        @Override
        public void onServerChannelConnectedFailure(ErrorCode errCode) {

        }
    }
}
