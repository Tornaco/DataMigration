package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.protocol.CategoryHeader;
import org.newstand.datamigration.net.protocol.OverviewHeader;
import org.newstand.datamigration.net.server.SocketClient;
import org.newstand.datamigration.sync.SharedExecutor;

import java.io.IOException;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderActivity extends TransactionSafeActivity implements SocketClient.ChannelHandler {

    @Setter
    @Getter
    SocketClient client;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startClient();
    }

    private void send() {

        Logger.d("Sending...");

        final LoadingCacheManager cacheManager = LoadingCacheManager.droid();

        // OH
        final OverviewHeader overviewHeader = OverviewHeader.empty();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.get(category);
                overviewHeader.add(category, records);
            }
        });

        try {
            OverViewSender.with(client.getInputStream(), client.getOutputStream()).send(overviewHeader);
        } catch (IOException e) {
            onError(e);
        }

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.get(category);

                CategoryHeader categoryHeader = CategoryHeader.from(category);
                categoryHeader.add(records);

                Logger.d("Sending header: " + categoryHeader);

                try {
                    CategorySender.with(client.getInputStream(), client.getOutputStream()).send(categoryHeader);
                } catch (IOException e) {
                    onError(e);
                }
            }
        });

    }


    private void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onServerChannelConnected() {
        send();
    }
}
