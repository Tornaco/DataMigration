package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.StartSignal;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.iwgang.simplifyspan.SimplifySpanBuild;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

// Importing delegate BK
public class DataImportManageFragment extends DataTransportManageFragment {

    private CountDownLatch mTaskLatch;

    private TransportListener mExportListener = new TransportListenerMainThreadAdapter() {
        @Override
        public void onStartMainThread() {
            super.onStartMainThread();
        }

        @Override
        public void onCompleteMainThread() {
            super.onCompleteMainThread();
            mTaskLatch.countDown();
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
            onProgressUpdate();
        }

        @Override
        public void onPieceStartMainThread(DataRecord record) {
            super.onPieceStartMainThread(record);
            showCurrentPieceInUI(record);
        }
    };

    private void showCurrentPieceInUI(DataRecord record) {
        getConsoleSummaryView().setText(record.getDisplayName());
    }

    public interface LoaderSourceProvider {
        LoaderSource onRequestLoaderSource();
    }

    private LoaderSourceProvider mLoaderSourceProvider;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoaderSourceProvider = (LoaderSourceProvider) getActivity();
    }

    @Override
    protected Session onCreateSession() {
        return mLoaderSourceProvider.onRequestLoaderSource().getSession();
    }

    private LoadingCacheManager getCache(LoaderSource source) {
        switch (source.getParent()) {
            case Android:
                return LoadingCacheManager.droid();
            case Backup:
                return LoadingCacheManager.bk();
            case Received:
                return LoadingCacheManager.received();
            default:
                throw new IllegalArgumentException("Bad source:" + source);
        }
    }

    @Override
    protected void readyToGo() {
        super.readyToGo();

        final LoadingCacheManager cache = getCache(mLoaderSourceProvider.onRequestLoaderSource());

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), getSession());

        mTaskLatch = Sleeper.waitingFor(DataCategory.values().length, new Runnable() {
            @Override
            public void run() {
                enterState(STATE_TRANSPORT_END);
            }
        });

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.checked(category);
                if (Collections.isNullOrEmpty(dataRecords)) {
                    mTaskLatch.countDown();// Release one!!!
                    return;
                }

                StartSignal startSignal = new StartSignal();
                startSignal.setTag(category);
                AbortSignal abortSignal = dataBackupManager.performRestoreAsync(dataRecords, category, mExportListener, startSignal);

                getStats().merge(mExportListener.getStats());

                getAbortSignals().add(abortSignal);
                getStartSignals().add(startSignal);
            }
        }, new Runnable() {
            @Override
            public void run() {
                Collections.consumeRemaining(getStartSignals(), new Consumer<StartSignal>() {
                    @Override
                    public void accept(@NonNull StartSignal startSignal) {

                        DataCategory category = (DataCategory) startSignal.getTag();

                        Logger.d("Tag of startSignal %s", startSignal.getTag());

                        if (category == DataCategory.Sms) {
                            // Set us as Def Sms app
                            SmsContentProviderCompat.setAsDefaultSmsApp(getActivity());
                            boolean isDefSmsApp = SmsContentProviderCompat.waitUtilBecomeDefSmsApp(getContext(), 10);// FIXME
                            if (!isDefSmsApp) {
                                Logger.e("Timeout waiting for DEF SMS APP setup, let it go~");
                            }
                        }

                        startSignal.start();
                    }
                });
            }
        });
    }

    @Override
    int getStartTitle() {
        return R.string.title_restore_importing;
    }

    @Override
    int getCompleteTitle() {
        return R.string.title_restore_import_complete;
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
}
