package org.newstand.datamigration.ui.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.ActionListenerMainThreadAdapter;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.service.DataSelectionKeeperServiceProxy;
import org.newstand.datamigration.ui.widget.ProgressWheel;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.BackupRestoreListener;
import org.newstand.datamigration.worker.backup.BackupRestoreListenerMainThreadAdapter;
import org.newstand.datamigration.worker.backup.DataBackupManager;
import org.newstand.datamigration.worker.backup.session.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class DataTransportFragment extends Fragment {

    private ProgressWheel progressBar;
    private TextView consoleTitleView, consoleSummaryView, consoleDoneButton;

    private final Set<AbortSignal> abortSignals = new HashSet<>();

    @Getter
    private Session session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.data_transporter, container, false);
        progressBar = (ProgressWheel) root.findViewById(R.id.progress_view);
        consoleTitleView = (TextView) root.findViewById(android.R.id.title);
        consoleSummaryView = (TextView) root.findViewById(android.R.id.text1);
        consoleDoneButton = (TextView) root.findViewById(R.id.button);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        start();
    }

    void start() {
        onPrepare();
        session = Session.create();
        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), session);
        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull final DataCategory category) {
                DataSelectionKeeperServiceProxy.getSelectionByCategoryAsync(getContext(), category,
                        new ActionListenerMainThreadAdapter<List<DataRecord>>(Looper.getMainLooper()) {
                            @Override
                            public void onActionMainThread(@Nullable List<DataRecord> dataRecords) {
                                if (Collections.isEmpty(dataRecords)) return;
                                AbortSignal signal = dataBackupManager.performRestore(dataRecords, category,
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
                            }
                        });
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
