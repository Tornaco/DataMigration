package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.ActionListener2;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.net.protocol.DataReceiverProxy;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.net.server.TransportServerProxy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverActivity extends TransitionSafeActivity implements TransportServer.ChannelHandler {

    @Getter
    @Setter
    private TransportServer transportServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startServer();
    }

    private void startServer() {
        String host = getIntent().getStringExtra(IntentEvents.KEY_HOST);
        int[] ports = SettingsProvider.getTransportServerPorts();
        TransportServerProxy.startWithPenitentialPortsAsync(host, ports, this, new Consumer<TransportServer>() {
            @Override
            public void accept(@NonNull TransportServer transportServer) {
                setTransportServer(transportServer);
            }
        });
    }

    void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onServerCreateFail(ErrorCode errCode) {

    }

    @Override
    public void onServerChannelCreate() {
        Logger.d("onServerChannelCreate @%s", transportServer.toString());
    }

    @Override
    public void onServerChannelStop() {
        Logger.d("onClientStop @%s", transportServer.toString());
    }

    @Override
    public void onClientChannelCreated() {
        DataReceiverProxy.receive(this, getTransportServer(), new ActionListener2<Void, Throwable>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete(Void aVoid) {

            }
        });
    }
}
