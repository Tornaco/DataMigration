package org.newstand.datamigration.ui.fragment;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.ui.widget.InputDialogCompat;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.DateUtils;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;

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

public class DataExportManageFragment extends DataTransportManageFragment {

    @Override
    protected void readyToGo() {
        super.readyToGo();

        final LoadingCacheManager cache = LoadingCacheManager.droid();

        final DataBackupManager dataBackupManager = DataBackupManager.from(getContext(), getSession());

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.checked(category);
                if (Collections.isNullOrEmpty(dataRecords)) {
                    return;
                }
                dataBackupManager.performBackup(mExportListener, dataRecords, category);
            }
        }, new Runnable() {
            @Override
            public void run() {
                enterState(STATE_TRANSPORT_END);
            }
        });
    }

    @Override
    protected Session onCreateSession() {
        return Session.from(getString(R.string.title_backup_default_name)
                + "-"
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

    // FIXME Save session.
    @Override
    public void onDestroy() {
        super.onDestroy();
        BKSessionRepoService.get().insert(getContext(), getSession());
    }

    @Override
    SimplifySpanBuild onCreateCompleteSummary() {
        SimplifySpanBuild summary = new SimplifySpanBuild();
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
}
