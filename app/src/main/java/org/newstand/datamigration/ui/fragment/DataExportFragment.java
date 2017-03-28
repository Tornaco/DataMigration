package org.newstand.datamigration.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.repo.BKSessionRepoServiceOneTime;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.BackupRestoreListener;
import org.newstand.datamigration.worker.backup.BackupRestoreListenerMainThreadAdapter;
import org.newstand.datamigration.worker.backup.DataBackupManager;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialClickableUnit;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataExportFragment extends DataTransportFragment {

    BackupRestoreListener listener = new BackupRestoreListenerMainThreadAdapter() {
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
    };

    @Override
    protected void start() {
        super.start();
        onPrepare();

        session = Session.create();

        final LoadingCacheManager cache = LoadingCacheManager.droid();

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), session);

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.get(category);
                if (Collections.nullOrEmpty(dataRecords)) return;

                final Collection<DataRecord> work = new ArrayList<>();

                Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
                    @Override
                    public void consume(@NonNull DataRecord dataRecord) {
                        if (dataRecord.isChecked()) work.add(dataRecord);
                    }
                });

                if (Collections.nullOrEmpty(work)) return;
                AbortSignal signal = dataBackupManager.performBackupAsync(work, category, listener);
                synchronized (abortSignals) {
                    abortSignals.add(signal);
                }
            }
        });
    }

    private void onPrepare() {
        consoleTitleView.setText(R.string.title_backup_exporting);
        consoleDoneButton.setText(android.R.string.cancel);
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
        Logger.d("progress:%s @%s", progress, status);
        progressBar.setProgress((int) (progress * 360));
    }

    private void onTransportComplete() {

        sendCompleteEvent();

        Logger.d("All complete, set to 100");
        progressBar.setText("100");
        progressBar.setProgress(360);
        consoleDoneButton.setText(R.string.action_done);
        consoleDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save session
                BKSessionRepoServiceOneTime.get().insert(session);
                getActivity().finish();
            }
        });
        consoleTitleView.setText(R.string.action_remark);

        updateCompleteSummary();
    }

    protected void updateCompleteSummary() {
        SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
        simplifySpanBuild.append(new SpecialTextUnit(session.getName())
                .setTextColor(ContextCompat.getColor(getContext(), R.color.accent))
                .showUnderline()
                .useTextBold()
                .setClickableUnit(new SpecialClickableUnit(consoleSummaryView, new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        showNameSettingsDialog(session.getName());
                    }
                })));

        consoleSummaryView.setText(simplifySpanBuild.build());
    }

    protected boolean validateInput(CharSequence in) {
        return !TextUtils.isEmpty(in) && !in.toString().contains("Tmp_")
                && !in.toString().contains(File.separator);
    }

    protected void showNameSettingsDialog(final String currentName) {
        new InputDialogCompat.Builder(getActivity())
                .setTitle(getString(R.string.action_remark))
                .setInputDefaultText(currentName)
                .setInputMaxWords(32)
                .setPositiveButton(getString(android.R.string.ok), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        DataBackupManager.from(getContext()).renameSessionChecked(session, inputText.toString());
                        updateCompleteSummary();
                    }
                })
                .interceptButtonAction(new InputDialogCompat.ButtonActionIntercepter() {
                    @Override
                    public boolean onInterceptButtonAction(int whichButton, CharSequence inputText) {
                        return !validateInput(inputText);
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        // Nothing.
                    }
                })
                .show();
    }
}
