package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.PathCreator;
import org.newstand.datamigration.net.protocol.CategoryHeader;
import org.newstand.datamigration.net.protocol.OverviewHeader;
import org.newstand.datamigration.net.server.SocketClient;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.IOException;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderActivity extends TransitionSafeActivity implements SocketClient.ChannelHandler {

    @Setter
    @Getter
    SocketClient client;

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

        final SocketClient client = new SocketClient();
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

        Logger.d("Sending...");

        final LoadingCacheManager cacheManager = LoadingCacheManager.droid();

        final Session session = Session.tmp();

        // OH
        final OverviewHeader overviewHeader = OverviewHeader.empty();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.checked(category);

                PathCreator.createIfNull(getApplicationContext(), session, records);

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
                Collection<DataRecord> records = cacheManager.checked(category);

                CategoryHeader categoryHeader = CategoryHeader.from(category);
                categoryHeader.add(records);

                Logger.d("Sending header: " + categoryHeader);

                try {
                    CategorySender.with(client.getInputStream(), client.getOutputStream()).send(categoryHeader);

                    Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                        @Override
                        public void consume(@NonNull DataRecord dataRecord) {
                            try {
                                int res = DataRecordSender.with(client.getOutputStream(), client.getInputStream())
                                        .send(dataRecord);
                            } catch (IOException e) {
                                onError(e);
                            }
                        }
                    });

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

    @Override
    public void onServerChannelConnectedFailure(int errCode) {

    }
}
