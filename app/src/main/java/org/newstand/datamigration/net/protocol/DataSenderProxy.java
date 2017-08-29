package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.BadResError;
import org.newstand.datamigration.net.CanceledError;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.IORES;
import org.newstand.datamigration.net.NextPlanSender;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Event;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerAdapter;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderProxy {

    @Setter
    @Getter
    Plans nextPlan = Plans.CONTINUE;

    private DataSenderProxy() {
    }

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final Session session,
                            final TransportListener listener, AbortSignal abortSignal) {
        new DataSenderProxy().sendInternal(context, client,
                EventRecorderTransportListenerProxy.delegate(context, listener, session,
                        TransportType.Send), abortSignal);
    }

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final Session session,
                            final TransportListener listener) {
        new DataSenderProxy().sendInternal(context, client, EventRecorderTransportListenerProxy
                .delegate(context, listener, session, TransportType.Send), new AbortSignal());
    }


    private void sendInternal(final Context context, final TransportClient transportClient,
                              final TransportListener transportListener, AbortSignal abortSignal) {

        abortSignal.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setNextPlan(Plans.CANCEL);
            }
        });

        transportListener.onEvent(Event.Prepare);

        final LoadingCacheManager cacheManager = LoadingCacheManager.droid();

        // Create a session, later we saved it to receiver.
        final Session session = Session.create();

        // Send overview header
        final OverviewHeader overviewHeader = OverviewHeader.empty();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.checked(category);
                Logger.v("Adding %s of size %s to overview header", category, records.size());
                overviewHeader.add(category, records);
            }
        });

        transportListener.onEvent(Event.ReadyToTransport);

        try {
            Logger.d("Sending overviewHeader: %s", overviewHeader);
            OverViewSender.with(transportClient.getInputStream(),
                    transportClient.getOutputStream()).send(overviewHeader);
        } catch (IOException e) {
            transportListener.onAbort(e);
            // Serious err.
            transportClient.stop();
            return;
        }

        transportListener.onStart();

        for (DataCategory category : DataCategory.values()) {
            Collection<DataRecord> records = cacheManager.checked(category);

            // Do not send anything if empty.
            if (Collections.isNullOrEmpty(records)) continue;


            // Send category header
            CategoryHeader categoryHeader = CategoryHeader.from(category);
            categoryHeader.add(records);

            Logger.d("Sending categoryHeader: %s, byte content:%s",
                    categoryHeader, Arrays.toString(categoryHeader.toBytes()));

            try {
                CategorySender.with(transportClient.getInputStream(), transportClient.getOutputStream()).send(categoryHeader);
                float pending = records.size();
                float sent = 0f;
                for (DataRecord dataRecord : records) {

                    try {
                        transportListener.onRecordStart(dataRecord);
                        DataRecordSender recordSender = DataRecordSender.with(transportClient.getOutputStream(),
                                transportClient.getInputStream(), session);
                        recordSender.wire(context);


                        try {
                            int res = recordSender.send(dataRecord);
                            if (res == IORES.OK) {
                                transportListener.onRecordSuccess(dataRecord);
                            } else {
                                transportListener.onRecordFail(dataRecord, new BadResError(res));
                            }
                        } catch (Throwable e) {
                            transportListener.onRecordFail(dataRecord, e);
                        } finally {
                            sent = sent + 1;
                            transportListener.onProgressUpdate((sent / pending) * 100);
                        }

                        // Send next plan, to cancel or continue?
                        NextPlanSender.with(transportClient.getInputStream(), transportClient.getOutputStream(), nextPlan).send(null);

                        if (nextPlan == Plans.CANCEL) {
                            transportListener.onAbort(new CanceledError());
                            transportClient.stop();
                            return;
                        }

                    } catch (IOException e) {
                        transportListener.onRecordFail(dataRecord, e);
                    }
                } // End for.

            } catch (final IOException e) {
                // Notify listener to abort
                transportListener.onAbort(e);
                break;
            }
        } // End for.

        transportListener.onComplete();

        transportClient.stop();

        abortSignal.deleteObservers();

        // Clean up Session
        org.newstand.datamigration.utils.Files.deleteDir(new File(SettingsProvider.getBackupSessionDir(session)));
    }


    private static class EventRecorderTransportListenerProxy extends TransportListenerAdapter {
        public static TransportListener delegate(Context context, TransportListener in, Session session, TransportType transportType) {
            return new EventRecorderTransportListenerProxy(context, in, session, transportType);
        }

        @Getter
        private Context context;

        private TransportListener listener;
        @Getter
        private Session session;

        @Getter
        private TransportType transportType;

        EventRecorderTransportListenerProxy(Context context, TransportListener listener, Session session, TransportType transportType) {
            this.context = context;
            this.listener = listener;
            this.session = session;
            this.transportType = transportType;
        }

        @Override
        public void onStart() {
            listener.onStart();
        }

        @Override
        public void onRecordStart(DataRecord record) {
            listener.onRecordStart(record);
        }

        @Override
        public void onRecordProgressUpdate(DataRecord record, RecordEvent recordEvent, float progress) {
            listener.onRecordProgressUpdate(record, recordEvent, progress);
        }

        @Override
        public void onProgressUpdate(float progress) {
            super.onProgressUpdate(progress);
            listener.onProgressUpdate(progress);
        }

        @Override
        public void onRecordSuccess(DataRecord record) {

            try {
                TransportEventRecord transportEventRecord = TransportEventRecord.builder()
                        .category(record.category())
                        .dataRecord(record)
                        .success(true)
                        .when(System.currentTimeMillis())
                        .build();

                TransportEventRecordRepoService.from(getSession(), getTransportType()).insert(getContext(), transportEventRecord);
            } catch (Throwable e) {
                Logger.e(e, "Fail insert event");
            }

            listener.onRecordSuccess(record);
        }

        @Override
        public void onRecordFail(DataRecord record, Throwable err) {
            try {
                TransportEventRecord transportEventRecord = TransportEventRecord.builder()
                        .category(record.category())
                        .dataRecord(record)
                        .success(false)
                        .errMessage(err.getMessage())
                        .errTrace(Logger.getStackTraceString(err))
                        .when(System.currentTimeMillis())
                        .build();
                TransportEventRecordRepoService.from(getSession(), getTransportType())
                        .insert(getContext(), transportEventRecord);
            } catch (Throwable e) {
                Logger.e(e, "Fail insert event");
            }

            listener.onRecordFail(record, err);
        }

        @Override
        public void onComplete() {
            listener.onComplete();
        }

        @Override
        public void onAbort(Throwable err) {
            listener.onAbort(err);
        }
    }
}
