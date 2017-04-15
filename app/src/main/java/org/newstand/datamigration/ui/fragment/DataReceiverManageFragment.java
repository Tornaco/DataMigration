package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.net.protocol.DataReceiverProxy;
import org.newstand.datamigration.net.server.ErrorCode;
import org.newstand.datamigration.net.server.ServerCreateFailError;
import org.newstand.datamigration.net.server.TransportServer;
import org.newstand.datamigration.net.server.TransportServerProxy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;
import org.newstand.logger.Logger;

import java.util.List;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverManageFragment extends DataTransportManageFragment
        implements TransportServer.ChannelHandler {

    // Receiver is not cancelable~
    @Setter
    private boolean isCancelable = false;

    @Getter
    @Setter
    private TransportServer transportServer;

    private Producer<String> mHostProducer;

    private TransportListener mTransportListener = new TransportListenerMainThreadAdapter() {
        @Override
        public void onStartMainThread() {
            super.onStartMainThread();
            // Merge two stats~
            DataReceiverManageFragment.this.getStats().merge(getStats());
        }

        @Override
        public void onCompleteMainThread() {
            super.onCompleteMainThread();
            enterState(STATE_TRANSPORT_END);
        }

        @Override
        public void onPieceFailMainThread(DataRecord record, Throwable err) {
            super.onPieceFailMainThread(record, err);
            onProgressUpdate();
            publishFailEventAsync(record, err);
        }

        @Override
        public void onPieceSuccessMainThread(DataRecord record) {
            super.onPieceSuccessMainThread(record);
            // Because we will never receive the startService event, we show current ui here.
            showCurrentPieceInUI(record);
            onProgressUpdate();
        }

        @Override
        public void onPieceStartMainThread(DataRecord record) {
            super.onPieceStartMainThread(record);
            showCurrentPieceInUI(record);
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
    };

    @Override
    public boolean isCancelable() {
        return isCancelable;
    }

    private void showCurrentPieceInUI(DataRecord record) {
        getConsoleSummaryView().setText(record.getDisplayName());
    }

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
        startServer();
    }

    private void startServer() {
        String host = mHostProducer.produce();
        int[] ports = SettingsProvider.getTransportServerPorts();
        TransportServerProxy.startWithPenitentialPortsAsync(host, ports,
                this, new Consumer<TransportServer>() {
                    @Override
                    public void accept(@NonNull TransportServer transportServer) {
                        setTransportServer(transportServer);
                    }
                });
    }

    private void receive() {
        DataReceiverProxy.receive(getActivity(), getTransportServer(), mTransportListener, getSession());
    }

    @Override
    int getStartTitle() {
        return R.string.title_restore_receiving;
    }

    @Override
    int getCompleteTitle() {
        return R.string.title_restore_receiving_complete;
    }

    @Override
    void onDoneButtonClick() {
        getActivity().finish();
    }

    @Override
    SimplifySpanBuild onCreateCompleteSummary() {
        return buildTransportReport(getStats());
    }

    @Override
    protected void onFailTextInSummaryClick() {
        super.onFailTextInSummaryClick();
        queryFailEventAsync(new Consumer<List<UserAction>>() {
            @Override
            public void accept(@NonNull final List<UserAction> userActions) {
                if (userActions.size() == 0) {
                    Logger.w("No user actions got~");
                    return;
                }
                final StringBuilder message = new StringBuilder();
                Collections.consumeRemaining(userActions, new Consumer<UserAction>() {
                    @Override
                    public void accept(@NonNull UserAction userAction) {
                        message.append(userAction.getEventDescription());
                    }
                });
                post(new Runnable() {
                    @Override
                    public void run() {
                        ErrDialog.attach(getActivity(), message.toString(), null);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmsContentProviderCompat.restoreDefSmsAppCheckedAsync(getContext());
    }

    @Override
    public void onServerCreateFail(final ErrorCode errCode) {
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

    @Override
    public void onServerChannelCreate() {
        Logger.d("onServerChannelCreate @%s", transportServer.toString());
    }

    @Override
    public void onServerChannelStop() {
        Logger.d("onClientStop @%s", transportServer.toString());
    }

    @Override
    public void onClientChannelCreated() {
        receive();
    }

}
