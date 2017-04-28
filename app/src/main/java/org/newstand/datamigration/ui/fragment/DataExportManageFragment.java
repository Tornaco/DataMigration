package org.newstand.datamigration.ui.fragment;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.StartSignal;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.DateUtils;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.TransportListenerMainThreadAdapter;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialClickableUnit;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataExportManageFragment extends DataTransportManageFragment {

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

    @Override
    protected void readyToGo() {
        super.readyToGo();

        final LoadingCacheManager cache = LoadingCacheManager.droid();

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
                AbortSignal abortSignal = dataBackupManager.performBackupAsync(dataRecords, category, mExportListener, startSignal);

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
                        startSignal.start();
                    }
                });
            }
        });
    }

    @Override
    protected Session onCreateSession() {
        return Session.from(getString(R.string.title_backup_default_name)
                + "@"
                + DateUtils.formatForFileName(System.currentTimeMillis()));
    }

    @Override
    int getStartTitle() {
        return R.string.title_backup_exporting;
    }

    @Override
    int getCompleteTitle() {
        return R.string.title_backup_export_complete;
    }

    @Override
    void onDoneButtonClick() {
        // Save session
        BKSessionRepoService.get().insert(getContext(), getSession());
        getActivity().finish();
    }

    private void showCurrentPieceInUI(DataRecord record) {
        getConsoleSummaryView().setText(record.getDisplayName());
    }

    @Override
    SimplifySpanBuild onCreateCompleteSummary() {
        SimplifySpanBuild summary = buildTransportReport(getStats());
        summary.append("\n\n");
        summary.append(getStringSafety(R.string.action_remark_backup));
        summary.append(new SpecialTextUnit(getSession().getName())
                .setTextColor(ContextCompat.getColor(getContext(), R.color.accent))
                .showUnderline()
                .useTextBold()
                .showUnderline()
                .setClickableUnit(new SpecialClickableUnit(getConsoleSummaryView(), new OnClickableSpanListener() {
                    @Override
                    public void onClick(TextView tv, String clickText) {
                        showNameSettingsDialog(getSession().getName());
                    }
                })));
        summary.append(getStringSafety(R.string.action_remark_tips));
        return summary;
    }

    protected boolean validateInput(String currentName, CharSequence in) {
        return !TextUtils.isEmpty(in) && (!currentName.equals(in.toString()))
                && !in.toString().contains("Tmp_")
                && !in.toString().contains(File.separator);
    }

    protected void showNameSettingsDialog(final String currentName) {
        new InputDialogCompat.Builder(getActivity())
                .setTitle(getString(R.string.action_remark_backup))
                .setInputDefaultText(currentName)
                .setInputMaxWords(32)
                .setPositiveButton(getString(android.R.string.ok), new InputDialogCompat.ButtonActionListener() {
                    @Override
                    public void onClick(CharSequence inputText) {
                        DataBackupManager.from(getContext())
                                .renameSessionChecked(
                                        LoaderSource.builder().parent(LoaderSource.Parent.Backup).build(),
                                        getSession(), inputText.toString().replace(" ", ""));
                        updateCompleteSummary();
                    }
                })
                .interceptButtonAction(new InputDialogCompat.ButtonActionIntercepter() {
                    @Override
                    public boolean onInterceptButtonAction(int whichButton, CharSequence inputText) {
                        return whichButton == DialogInterface.BUTTON_POSITIVE
                                && !validateInput(currentName, inputText);
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
}
