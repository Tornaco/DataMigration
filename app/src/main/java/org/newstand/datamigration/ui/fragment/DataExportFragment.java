package org.newstand.datamigration.ui.fragment;

import android.support.annotation.NonNull;
import android.view.View;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.cache.SelectionCache;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.BackupRestoreListener;
import org.newstand.datamigration.worker.backup.BackupRestoreListenerMainThreadAdapter;
import org.newstand.datamigration.worker.backup.DataBackupManager;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataExportFragment extends DataTransportFragment {

    @Override
    protected void start() {
        super.start();
        onPrepare();

        session = Session.create();

        final SelectionCache cache = SelectionCache.from(getContext());

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), session);

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                try {
                    Collection<DataRecord> dataRecords = cache.fromSource(LoaderSource.builder()
                                    .parent(LoaderSource.Parent.Android)
                                    .session(null).build(),
                            getContext()).get(category);
                    if (Collections.isEmpty(dataRecords)) return;

                    AbortSignal signal = dataBackupManager.performBackup(dataRecords, category,
                            new BackupRestoreListenerMainThreadAdapter() {
                                @Override
                                public void onStartMainThread() {
                                    super.onStartMainThread();
                                }

                                @Override
                                public void onCompleteMainThread() {
                                    super.onCompleteMainThread();
                                    onTransportComplete();
                                }

                                @Override
                                public void onPieceFailMainThread(DataRecord record, Throwable err) {
                                    super.onPieceFailMainThread(record, err);
                                    updateProgress(getStatus());
                                }

                                @Override
                                public void onPieceSuccessMainThread(DataRecord record) {
                                    super.onPieceSuccessMainThread(record);
                                    updateProgress(getStatus());
                                }

                                @Override
                                public void onPieceStartMainThread(DataRecord record) {
                                    super.onPieceStartMainThread(record);
                                    consoleSummaryView.setText(record.getDisplayName());
                                }
                            });
                    synchronized (abortSignals) {
                        abortSignals.add(signal);
                    }

                } catch (ExecutionException e) {
                    // FIXME
                }
            }
        });
    }

    private void onPrepare() {
        consoleTitleView.setText("Transporting");
        consoleDoneButton.setText("Cancel");
        consoleDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (abortSignals) {
                    Collections.consumeRemaining(abortSignals, new Consumer<AbortSignal>() {
                        @Override
                        public void consume(@NonNull AbortSignal abortSignal) {
                            abortSignal.abort();
                        }
                    });
                }
            }
        });
    }

    private void updateProgress(BackupRestoreListener.Status status) {
        float total = (float) status.getTotal();
        float ok = (float) status.getTotal() - (float) status.getLeft();
        float progress = (ok / total);
        progressBar.setText((int) (progress * 100) + "");
        Logger.d("progress:%s", progress);
        progressBar.setProgress((int) (progress * 360));
    }

    private void onTransportComplete() {
        consoleDoneButton.setText("Done");
        consoleDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        consoleTitleView.setText("Using remark:");
        consoleSummaryView.setText(session.getName());
    }
}
