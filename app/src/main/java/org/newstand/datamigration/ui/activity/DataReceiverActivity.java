package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.net.CategoryReceiver;
import org.newstand.datamigration.net.OverviewReceiver;
import org.newstand.datamigration.net.server.SocketClient;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;

import java.io.IOException;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverActivity extends TransactionSafeActivity implements SocketClient.ChannelHandler {

    @Setter
    @Getter
    SocketClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startClient();
    }

    void startClient() {
        String host = getIntent().getStringExtra("host");

        SocketClient client = new SocketClient();
        client.setHost(host);
        client.setPort(8899);

        client.setChannelHandler(this);
        SharedExecutor.execute(client);

        setClient(client);
    }

    @Override
    public void onServerChannelConnected() {
        OverviewReceiver overviewReceiver = OverviewReceiver.with(client.getInputStream(), client.getOutputStream());
        try {
            overviewReceiver.receive(null);
        } catch (IOException e) {
            onError(e);
        }

        Set<DataCategory> dataCategories = overviewReceiver.getHeader().getDataCategories();

        Collections.consumeRemaining(dataCategories, new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                CategoryReceiver categoryReceiver = CategoryReceiver.with(client.getInputStream(), client.getOutputStream());
                try {
                    categoryReceiver.receive(null);

                    Logger.d("Received header: " + categoryReceiver.getHeader());
                } catch (IOException e) {
                    onError(e);
                }
            }
        });
    }

    void onError(Throwable e) {
        e.printStackTrace();
    }
}
