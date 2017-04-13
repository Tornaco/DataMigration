package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.ActionListener2Delegate;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.net.protocol.DataSenderProxy;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderActivity extends TransitionSafeActivity implements TransportClient.ChannelHandler {

    @Setter
    @Getter
    TransportClient client;

    private void startClient() {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int[] ports = SettingsProvider.getTransportServerPorts();
                for (int port : ports) {
                    if (startClientWith(port)) {
                        return;
                    }
                }
            }
        });
    }

    boolean startClientWith(int port) {
        String host = getIntent().getStringExtra(IntentEvents.KEY_HOST);

        final TransportClient client = new TransportClient();
        client.setHost(host);
        client.setPort(port);

        client.setChannelHandler(this);

        setClient(client);

        return client.start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startClient();
    }

    private void send() {
        DataSenderProxy.send(getApplicationContext(), getClient(), new ActionListener2Delegate<>(new ActionListener2Adapter<Void, Throwable>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                throwable.printStackTrace();
            }
        }, Looper.getMainLooper()));
    }

    @Override
    public void onServerChannelConnected() {
        send();
    }

    @Override
    public void onServerChannelConnectedFailure(ErrorCode errCode) {

    }
}
