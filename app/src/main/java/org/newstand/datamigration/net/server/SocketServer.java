package org.newstand.datamigration.net.server;

import com.google.common.io.Closer;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.sync.SharedExecutor;

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

public class SocketServer extends ServerComponent {

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
                    channelHandler.onServerCreateFail(ChannelHandler.FAIL_ACCEPT_FAIL);
                    return;
                }

                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    setInputStream(closer.register(inputStream));
                    setOutputStream(closer.register(outputStream));

                    if (channelHandler != null) channelHandler.onClientChannelCreated();

                } catch (IOException e) {
                    channelHandler.onServerCreateFail(ChannelHandler.FAIL_RETRIEVE_STREAM);
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
            channelHandler.onServerCreateFail(ChannelHandler.FAIL_ADDRESS_IN_USE);
        } catch (IOException e) {
            channelHandler.onServerCreateFail(ChannelHandler.FAIL_UNKNOWN);
        }
        return serverSocket;
    }

    @Override
    public boolean stop() {
        try {
            closer.close();
        } catch (IOException e) {
            Logger.e("Close fail %s", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public interface ChannelHandler {

        int FAIL_ADDRESS_IN_USE = 0x1;
        int FAIL_ACCEPT_FAIL = 0x2;
        int FAIL_RETRIEVE_STREAM = 0x3;
        int FAIL_UNKNOWN = 0x4;


        void onServerCreateFail(int errCode);

        void onServerChannelCreate();

        void onClientChannelCreated();
    }
}
