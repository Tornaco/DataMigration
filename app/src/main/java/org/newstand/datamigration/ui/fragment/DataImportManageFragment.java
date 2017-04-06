package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.StartSignal;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.BackupRestoreListener;
import org.newstand.datamigration.worker.backup.BackupRestoreListenerMainThreadAdapter;
import org.newstand.datamigration.worker.backup.DataBackupManager;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import cn.iwgang.simplifyspan.SimplifySpanBuild;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

// Importing from BK
public class DataImportManageFragment extends DataTransportManageFragment {

    private CountDownLatch mTaskLatch;

    private BackupRestoreListener mExportListener = new BackupRestoreListenerMainThreadAdapter() {
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

    @Override
    protected void readyToGo() {
        super.readyToGo();

        final LoadingCacheManager cache = LoadingCacheManager.bk();

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), getSession());

        mTaskLatch = Sleeper.waitingFor(DataCategory.values().length, new Runnable() {
            @Override
            public void run() {
                enterState(STATE_TRANSPORT_END);
            }
        });

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.checked(category);
                if (Collections.nullOrEmpty(dataRecords)) {
                    mTaskLatch.countDown();// Release one!!!
                    return;
                }

                StartSignal startSignal = new StartSignal();
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
                    public void consume(@NonNull StartSignal startSignal) {
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
}