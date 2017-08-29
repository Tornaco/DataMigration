package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.Holder;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.BadResError;
import org.newstand.datamigration.net.CanceledError;
import org.newstand.datamigration.net.CategoryReceiver;
import org.newstand.datamigration.net.DataRecordReceiver;
import org.newstand.datamigration.net.IORES;
import org.newstand.datamigration.net.NextPlanReceiver;
import org.newstand.datamigration.net.OverviewReceiver;
import org.newstand.datamigration.net.ReceiveSettings;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerAdapter;
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.Set;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/14 10:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverProxy {

    @WorkerThread
    public static void receive(final Context context, final TransportServer transportServer,
                               final TransportListener listener, Session session) {
        receiveInternal(context, transportServer, EventRecorderTransportListenerProxy
                .delegate(context, listener, session, TransportType.Receive), session);
    }

    @WorkerThread
    public static void receive(final Context context, final TransportServer transportServer,
                               final TransportListener listener) {
        Session session = Session.create();
        receiveInternal(context, transportServer, EventRecorderTransportListenerProxy
                .delegate(context, listener, session, TransportType.Receive), session);
    }

    private static void receiveInternal(final Context context, final TransportServer transportServer,
                                        final TransportListener listener, Session session) {

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
            // Overview header fail, can not move~
            listener.onAbort(e);
            transportServer.stop();
            return;
        }

        listener.onStart();

        Set<DataCategory> dataCategories = overviewReceiver.getHeader().getDataCategories();

        int CATEGORY_SIZE = dataCategories.size();

        for (int i = 0; i < CATEGORY_SIZE; i++) {

            CategoryReceiver categoryReceiver = CategoryReceiver.with(transportServer.getInputStream(),
                    transportServer.getOutputStream());
            try {
                categoryReceiver.receive(null);
                Logger.d("Received header: " + categoryReceiver.getHeader());

                CategoryHeader categoryHeader = categoryReceiver.getHeader();

                final DataCategory category = categoryHeader.getDataCategory();

                float FILE_COUNT = categoryHeader.getFileCount();

                ReceiveSettings settings = new ReceiveSettings();

                final Holder<String> fileNameHolder = new Holder<>();

                settings.setNameConsumer(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) {
                        fileNameHolder.setData(s);
                    }
                });

                settings.setCategory(category);

                settings.setRootDir(SettingsProvider.getReceivedDirByCategory(category, session));

                for (float c = 0; c < FILE_COUNT; c++) {

                    try {
                        int res = DataRecordReceiver.with(transportServer.getInputStream(),
                                transportServer.getOutputStream())
                                .receive(settings);
                        DataRecord record = new DataRecord() {
                            @Override
                            public DataCategory category() {
                                return category;
                            }
                        };
                        record.setDisplayName(fileNameHolder.getData());
                        if (res == IORES.OK) {
                            listener.onRecordSuccess(record);

                        } else {
                            listener.onRecordFail(record, new BadResError(res));
                        }
                    } catch (Throwable e) {
                        Logger.e(e, "Record receive fail"); // FIXME Handle this?
                    } finally {
                        listener.onProgressUpdate((c / FILE_COUNT) * 100);
                    }
                    // Check next plan~
                    NextPlanReceiver nextPlanReceiver = NextPlanReceiver.with(transportServer.getInputStream(),
                            transportServer.getOutputStream());
                    nextPlanReceiver.receive(null);
                    Plans plan = nextPlanReceiver.getPlan();

                    Logger.i("Next plan %s", plan);

                    if (plan == Plans.CANCEL) {
                        listener.onAbort(new CanceledError());
                        transportServer.stop();
                        return;
                    }
                } // End for
            } catch (IOException e) {
                listener.onAbort(e);
            }
        } // End for

        listener.onComplete();

        transportServer.stop();
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
