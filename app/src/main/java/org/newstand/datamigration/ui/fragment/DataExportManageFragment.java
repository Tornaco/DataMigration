package org.newstand.datamigration.ui.fragment;

import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.DateUtils;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.datamigration.worker.transport.backup.TransportType;

import java.util.Collection;

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

        final TransportListener listener = onCreateTransportListener();

        DataCategory.consumeAllInWorkerThread(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> dataRecords = cache.checked(category);
                if (Collections.isNullOrEmpty(dataRecords)) {
                    return;
                }
                dataBackupManager.performBackup(listener, dataRecords, category);
            }
        }, new Runnable() {
            @Override
            public void run() {
                BKSessionRepoService.get().insert(getContext(), getSession());
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

    @Override
    String onCreateCompleteSummary() {
        return getStringSafety(R.string.action_remark_backup, getSession().getName());
    }

    @Override
    TransportType getTransportType() {
        return TransportType.Backup;
    }
}
