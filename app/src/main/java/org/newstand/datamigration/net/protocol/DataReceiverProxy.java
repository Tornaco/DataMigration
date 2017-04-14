package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.WorkerThread;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.common.ActionListener2;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.net.CategoryReceiver;
import org.newstand.datamigration.net.DataRecordReceiver;
import org.newstand.datamigration.net.OverviewReceiver;
import org.newstand.datamigration.net.ReceiveSettings;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Nick@NewStand.org on 2017/4/14 10:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverProxy {

    @WorkerThread
    public static void receive(final Context context, final TransportServer transportServer,
                               final ActionListener2<Void, Throwable> listener) {
        receiveInternal(context, transportServer, listener);
    }

    private static void receiveInternal(final Context context, final TransportServer transportServer,
                                        final ActionListener2<Void, Throwable> listener) {

        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(transportServer);
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(transportServer.getInputStream());
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(transportServer.getOutputStream());

        OverviewReceiver overviewReceiver = OverviewReceiver.with(transportServer.getInputStream(), transportServer.getOutputStream());
        try {
            overviewReceiver.receive(null);
        } catch (IOException e) {
            listener.onError(e);
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
                listener.onError(e);
            }
        }

        listener.onComplete(null);
    }
}
