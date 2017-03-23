package org.newstand.datamigration.net.server;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.utils.Closer;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/22 15:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SocketClient extends ServerComponent {

    @Getter
    @Setter
    private ChannelHandler channelHandler;

    @Override
    public void start() throws IOException {

        Logger.d("Starting client %s", toString());

        Socket socket = new Socket(getHost(), getPort());

        setOutputStream(socket.getOutputStream());
        setInputStream(socket.getInputStream());

        if (channelHandler != null) channelHandler.onServerChannelConnected();
    }

    @Override
    public void stop() {
        Closer.closeQuietly(getOutputStream());
        Closer.closeQuietly(getInputStream());
    }

    public interface ChannelHandler {
        void onServerChannelConnected();
    }
}
