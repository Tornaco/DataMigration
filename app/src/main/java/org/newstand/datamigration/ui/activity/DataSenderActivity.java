package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.ActionListener2Delegate;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.net.protocol.DataSenderProxy;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.net.server.TransportClientProxy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

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
        int[] ports = SettingsProvider.getTransportServerPorts();
        String host = getIntent().getStringExtra(IntentEvents.KEY_HOST);
        TransportClientProxy.startWithPenitentialPortsAsync(host, ports, this, new Consumer<TransportClient>() {
            @Override
            public void accept(@NonNull TransportClient transportClient) {
                setClient(transportClient);
            }
        });
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
    public void onClientStop() {
        Logger.d("onClientStop");
    }

    @Override
    public void onServerChannelConnectedFailure(ErrorCode errCode) {

    }
}
