package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.Producer;
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
import org.newstand.datamigration.worker.transport.backup.TransportType;
import org.newstand.logger.Logger;

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

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    private LoaderSourceProvider mLoaderSourceProvider;

    @Override
    TransportType getTransportType() {
        return TransportType.Send;
    }

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

    private void startClient() {
        transportListener = new TransportListenerDelegate(onCreateTransportListener());

        int[] ports = SettingsProvider.getTransportServerPorts();
        String host = mHostProducer.produce();
        TransportClientProxy.startWithPenitentialPortsAsync(host, ports, this, new Consumer<TransportClient>() {
            @Override
            public void accept(@NonNull TransportClient transportClient) {
                setClient(transportClient);
            }
        });
    }

    private TransportListener transportListener;

    private void send() {
        AbortSignal abortSignal = new AbortSignal();
        DataSenderProxy.send(getActivity(), getClient(), getSession(),
                transportListener, abortSignal);
        enterState(STATE_TRANSPORT_END);
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
    String onCreateCompleteSummary() {
        return "";
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

    private class TransportListenerDelegate extends TransportListenerMainThreadAdapter {
        TransportListenerMainThreadAdapter listener;

        TransportListenerDelegate(TransportListenerMainThreadAdapter listener) {
            this.listener = listener;
        }

        @Override
        public void onStartMainThread() {
            listener.onStartMainThread();
        }

        @Override
        public void onRecordStartMainThread(DataRecord record) {
            listener.onRecordStartMainThread(record);
        }

        @Override
        public void onRecordProgressUpdateMainThread(DataRecord record, RecordEvent recordEvent, float progress) {
            listener.onRecordProgressUpdateMainThread(record, recordEvent, progress);
        }

        @Override
        public void onRecordSuccessMainThread(DataRecord record) {
            listener.onRecordSuccessMainThread(record);
        }

        @Override
        public void onRecordFailMainThread(DataRecord record, Throwable err) {
            listener.onRecordFailMainThread(record, err);
        }

        @Override
        public void onProgressUpdateMainThread(float progress) {
            listener.onProgressUpdateMainThread(progress);
        }

        @Override
        public void onCompleteMainThread() {
            listener.onCompleteMainThread();
        }

        @Override
        public void onAbortMainThread(Throwable err) {
            listener.onAbortMainThread(err);

            ErrDialog.attach(getActivity(), err,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getActivity();
                            transitionSafeActivity.finish();
                        }
                    });
        }
    }
}
