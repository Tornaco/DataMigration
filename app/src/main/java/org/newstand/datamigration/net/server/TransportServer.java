package org.newstand.datamigration.net.server;

import com.google.common.io.Closer;

import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 13:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class TransportServer extends ServerComponent {

    @Getter
    private Closer closer = Closer.create();

    @Getter
    @Setter
    private ChannelHandler channelHandler;

    @Override
    public boolean start() {

        Logger.d("Starting server %s", toString());

        ServerSocket serverSocket = onCreateServerSocket();

        if (serverSocket == null) return false;

        onServerChannelCreate(closer.register(serverSocket));

        return true;
    }

    private void onServerChannelCreate(final ServerSocket serverSocket) {
        if (channelHandler != null) channelHandler.onServerChannelCreate();

        Runnable acceptor = new Runnable() {
            @Override
            public void run() {

                Socket socket;

                try {
                    socket = closer.register(serverSocket.accept());
                    Logger.d("Accepted: %s", socket.getInetAddress());

                } catch (IOException e) {
                    channelHandler.onServerCreateFail(ErrorCode.ERROR_ACCEPT_FAIL);
                    return;
                }

                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    setInputStream(closer.register(inputStream));
                    setOutputStream(closer.register(outputStream));

                    if (channelHandler != null) channelHandler.onClientChannelCreated();

                } catch (IOException e) {
                    channelHandler.onServerCreateFail(ErrorCode.ERROR_RETRIEVE_STREAM);
                }
            }
        };

        SharedExecutor.execute(acceptor);
    }

    private ServerSocket onCreateServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (BindException e) {
            channelHandler.onServerCreateFail(ErrorCode.ERROR_ADDRESS_IN_USE);
        } catch (IOException e) {
            channelHandler.onServerCreateFail(ErrorCode.ERROR_UNKNOWN);
        }
        return serverSocket;
    }

    @Override
    public boolean stop() {
        try {
            closer.close();
            channelHandler.onServerChannelStop();
        } catch (IOException e) {
            Logger.e("Close fail %s", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public interface ChannelHandler {

        void onServerCreateFail(ErrorCode errCode);

        void onServerChannelCreate();

        void onServerChannelStop();

        void onClientChannelCreated();
    }

    public static class ChannelHandlerAdapter implements ChannelHandler {

        @Override
        public void onServerCreateFail(ErrorCode errCode) {

        }

        @Override
        public void onServerChannelCreate() {

        }

        @Override
        public void onServerChannelStop() {

        }

        @Override
        public void onClientChannelCreated() {

        }
    }
}
