package org.newstand.datamigration.net.server;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.utils.Closer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    @Setter
    private ChannelHandler channelHandler;

    @Override
    public void start() throws IOException {

        Logger.d("Starting server %s", toString());

        ServerSocket serverSocket = new ServerSocket(getPort());
        // serverSocket.bind(new InetSocketAddress(getHost(), getPort()));

        if (channelHandler != null) channelHandler.onServerChannelCreate();

        Socket socket = serverSocket.accept();

        Logger.d("Accepted: %s", socket.getInetAddress());

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        setInputStream(inputStream);
        setOutputStream(outputStream);

        if (channelHandler != null) channelHandler.onClientChannelCreated();
    }


    @Override
    public void stop() {
        Closer.closeQuietly(getInputStream());
        Closer.closeQuietly(getOutputStream());
    }

    public interface ChannelHandler {
        void onServerChannelCreate();

        void onClientChannelCreated();
    }
}
