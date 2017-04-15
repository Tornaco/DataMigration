package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.BadResError;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.IORES;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.PathCreator;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.Stats;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderProxy {

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final TransportListener listener) {
        sendInternal(context, client, listener);
    }

    private static void sendInternal(final Context context, final TransportClient transportClient,
                                     final TransportListener transportListener) {

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
            OverViewSender.with(transportClient.getInputStream(), transportClient.getOutputStream()).send(overviewHeader);
        } catch (IOException e) {
            transportListener.onAbort(e);
            // Serious err.
            return;
        }

        // Init stats
        final SimpleStats stats = new SimpleStats();
        stats.init(overviewHeader.getFileCount());
        transportListener.setStats(stats);

        transportListener.onStart();

        for (DataCategory category : DataCategory.values()) {
            Collection<DataRecord> records = cacheManager.checked(category);

            // Do not send anything if empty.
            if (Collections.isNullOrEmpty(records)) continue;

            // Send category header
            CategoryHeader categoryHeader = CategoryHeader.from(category);
            categoryHeader.add(records);

            Logger.d("Sending categoryHeader: %s", categoryHeader);

            try {
                CategorySender.with(transportClient.getInputStream(), transportClient.getOutputStream()).send(categoryHeader);

                Collections.consumeRemaining(records, new Consumer<DataRecord>() {
                    @Override
                    public void accept(@NonNull DataRecord dataRecord) {
                        try {
                            transportListener.onPieceStart(dataRecord);
                            int res = DataRecordSender.with(transportClient.getOutputStream(), transportClient.getInputStream())
                                    .send(dataRecord);
                            if (res == IORES.OK) {
                                stats.onSuccess();
                                transportListener.onPieceSuccess(dataRecord);
                            } else {
                                transportListener.onPieceFail(dataRecord, new BadResError(res));
                                stats.onFail();
                            }
                        } catch (IOException e) {
                            transportListener.onPieceFail(dataRecord, e);
                            stats.onFail();
                        }
                    }
                });

            } catch (final IOException e) {
                // Notify listener to abort
                transportListener.onAbort(e);
                break;
            }
        }

        transportListener.onComplete();

        transportClient.stop();
    }

    @ToString
    private static class SimpleStats implements Stats {

        @Setter(AccessLevel.PACKAGE)
        @Getter
        private int total, left, success, fail;

        private void init(int size) {
            total = left = size;
            Logger.d("init status %s", toString());
        }

        private void onPiece() {
            left--;
        }

        @Override
        public void onSuccess() {
            success++;
            onPiece();
        }

        @Override
        public void onFail() {
            fail++;
            onPiece();
        }

        @Override
        public Stats merge(Stats with) {

            total += with.getTotal();
            left += with.getLeft();
            success += with.getSuccess();
            fail += with.getFail();

            return this;
        }
    }
}
