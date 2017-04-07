package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.net.CategoryReceiver;
import org.newstand.datamigration.net.DataRecordReceiver;
import org.newstand.datamigration.net.OverviewReceiver;
import org.newstand.datamigration.net.ReceiveSettings;
import org.newstand.datamigration.net.protocol.CategoryHeader;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Set;

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
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int[] ports = SettingsProvider.getTransportServerPorts();
                for (int port : ports) {
                    if (startServerWith(port)) {
                        return;
                    }
                }
            }
        });
    }

    private boolean startServerWith(int port) {

        String host = getIntent().getStringExtra(IntentEvents.KEY_HOST);

        TransportServer transportServer = new TransportServer();
        transportServer.setChannelHandler(DataReceiverActivity.this);
        transportServer.setHost(host);
        transportServer.setPort(port);

        setTransportServer(transportServer);

        return transportServer.start();
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
    public void onClientChannelCreated() {
        OverviewReceiver overviewReceiver = OverviewReceiver.with(transportServer.getInputStream(), transportServer.getOutputStream());
        try {
            overviewReceiver.receive(null);
        } catch (IOException e) {
            onError(e);
        }

        Set<DataCategory> dataCategories = overviewReceiver.getHeader().getDataCategories();

        Session session = Session.create();

        int N = dataCategories.size();

        for (int i = 0; i < N; i++) {
            CategoryReceiver categoryReceiver = CategoryReceiver.with(transportServer.getInputStream(), transportServer.getOutputStream());
            try {
                categoryReceiver.receive(null);
                Logger.d("Received header: " + categoryReceiver.getHeader());

                CategoryHeader categoryHeader = categoryReceiver.getHeader();

                DataCategory category = categoryHeader.getDataCategory();

                int C = categoryHeader.getFileCount();

                ReceiveSettings settings = new ReceiveSettings();

                settings.setDestDir(SettingsProvider.getReceivedDirByCategory(category, session));

                for (int c = 0; c < C; c++) {
                    int res = DataRecordReceiver.with(transportServer.getInputStream(), transportServer.getOutputStream())
                            .receive(settings);
                    Logger.d("Receive res %d", res);
                }
            } catch (IOException e) {
                onError(e);
            }
        }
    }
}
