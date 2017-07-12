package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.DataSenderProxy;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.ServerCreateFailError;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.net.server.TransportClientProxy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;
import org.newstand.logger.Logger;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderManageFragment extends DataTransportManageFragment
        implements TransportClient.ChannelHandler {

    @Setter
    private boolean isCancelable;

    @Setter
    @Getter
    TransportClient client;

    private Producer<String> mHostProducer;

    private TransportListener mTransportListener = new TransportListenerMainThreadAdapter() {

        @Override
        public void onStartMainThread() {
            super.onStartMainThread();
        }

        @Override
        public void onCompleteMainThread() {
            super.onCompleteMainThread();
            enterState(STATE_TRANSPORT_END);
        }

        @Override
        public void onRecordFailMainThread(DataRecord record, Throwable err) {
            super.onRecordFailMainThread(record, err);
        }

        @Override
        public void onRecordSuccessMainThread(DataRecord record) {
            super.onRecordSuccessMainThread(record);
        }

        @Override
        public void onRecordStartMainThread(DataRecord record) {
            super.onRecordStartMainThread(record);
            // Sub record is sending, we can cancel now~
            setCancelable(true);
            showCurrentRecordInUI(record);
        }

        @Override
        public void onRecordProgressUpdateMainThread(DataRecord record, RecordEvent recordEvent, float progress) {
            super.onRecordProgressUpdateMainThread(record, recordEvent, progress);
            showRecordProgressInUI(record, recordEvent, progress);
        }

        @Override
        public void onAbortMainThread(Throwable err) {
            super.onAbortMainThread(err);
            ErrDialog.attach(getActivity(), err,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
                            transitionSafeActivity.finish();
                        }
                    });
        }

        @Override
        public void onProgressUpdateMainThread(float progress) {
            super.onProgressUpdateMainThread(progress);
            updateProgressWheel(progress);
        }
    };


    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    private LoaderSourceProvider mLoaderSourceProvider;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoaderSourceProvider = (LoaderSourceProvider) getActivity();
        mHostProducer = (Producer<String>) getActivity();
    }

    @Override
    protected Session onCreateSession() {
        return mLoaderSourceProvider.onRequestLoaderSource().getSession();
    }

    @Override
    protected void readyToGo() {
        super.readyToGo();
        startClient();
    }

    @Override
    public boolean isCancelable() {
        return isCancelable;
    }

    private void startClient() {
        int[] ports = SettingsProvider.getTransportServerPorts();
        String host = mHostProducer.produce();
        TransportClientProxy.startWithPenitentialPortsAsync(host, ports, this, new Consumer<TransportClient>() {
            @Override
            public void accept(@NonNull TransportClient transportClient) {
                setClient(transportClient);
            }
        });
    }

    private void send() {
        AbortSignal abortSignal = new AbortSignal();
        DataSenderProxy.send(getActivity(), getClient(), mTransportListener, abortSignal);
    }

    @Override
    int getStartTitle() {
        return R.string.title_restore_sending;
    }

    @Override
    int getCompleteTitle() {
        return R.string.title_restore_sending_complete;
    }

    @Override
    SimplifySpanBuild onCreateCompleteSummary() {
        return new SimplifySpanBuild();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmsContentProviderCompat.restoreDefSmsAppCheckedAsync(getContext());
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
    public void onServerChannelConnectedFailure(final ErrorCode errCode) {
        post(new Runnable() {
            @Override
            public void run() {
                ErrDialog.attach(getActivity(), new ServerCreateFailError(errCode),
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
                                transitionSafeActivity.finish();
                            }
                        });
            }
        });
    }
}
