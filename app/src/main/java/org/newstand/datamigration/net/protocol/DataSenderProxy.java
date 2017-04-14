package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.ActionListener2;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.PathCreator;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderProxy {

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final ActionListener2<Void, Throwable> listener) {
        sendInternal(context, client, listener);
    }

    private static void sendInternal(final Context context, final TransportClient client,
                                     final ActionListener2<Void, Throwable> listener) {

        listener.onStart();

        final LoadingCacheManager cacheManager = LoadingCacheManager.droid();

        // Create a session, later we saved it to receiver.
        final Session session = Session.create();

        // Send overview header
        final OverviewHeader overviewHeader = OverviewHeader.empty();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.checked(category);

                PathCreator.createIfNull(context, session, records);

                overviewHeader.add(category, records);
            }
        });

        try {
            Logger.d("Sending overviewHeader: %s", overviewHeader);
            OverViewSender.with(client.getInputStream(), client.getOutputStream()).send(overviewHeader);
        } catch (IOException e) {
            listener.onError(e);
        }

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.checked(category);

                // Do not send anything if empty.
                if (Collections.isNullOrEmpty(records)) return;

                // Send category header
                CategoryHeader categoryHeader = CategoryHeader.from(category);
                categoryHeader.add(records);

                Logger.d("Sending categoryHeader: %s", categoryHeader);

                try {
                    CategorySender.with(client.getInputStream(), client.getOutputStream()).send(categoryHeader);

                    Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                        @Override
                        public void accept(@NonNull DataRecord dataRecord) {
                            try {
                                int res = DataRecordSender.with(client.getOutputStream(), client.getInputStream())
                                        .send(dataRecord);
                            } catch (IOException e) {
                                listener.onError(e);
                            }
                        }
                    });

                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        });

        listener.onComplete(null);
    }

}
