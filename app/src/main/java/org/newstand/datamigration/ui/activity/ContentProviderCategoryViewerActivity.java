package org.newstand.datamigration.ui.activity;

import android.app.ProgressDialog;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.ActionListenerMainThreadAdapter;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.service.DataSelectionKeeperServiceProxy;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.backup.BackupRestoreListenerMainThreadAdapter;
import org.newstand.datamigration.worker.backup.DataBackupManager;

import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ContentProviderCategoryViewerActivity extends CategoryViewerActivity {

    @Override
    public void onSubmit() {
        super.onSubmit();

        Logger.d("onSubmit");

        final ProgressDialog dialog = ProgressDialogCompat.createUnCancelableIndeterminate(ContentProviderCategoryViewerActivity.this);

        final DataBackupManager backupManager = DataBackupManager.from(this);

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void consume(@NonNull final DataCategory category) {

                DataSelectionKeeperServiceProxy.getSelectionByCategoryAsync(getApplicationContext(),
                        category,
                        new ActionListenerMainThreadAdapter<List<DataRecord>>(Looper.getMainLooper()) {
                            @Override
                            public void onActionMainThread(@Nullable List<DataRecord> dataRecords) {
                                if (Collections.isEmpty(dataRecords)) {
                                    Logger.e("No data got...");
                                    return;// FIXME handle err.
                                }
                                backupManager.performBackup(dataRecords, category,
                                        new BackupRestoreListenerMainThreadAdapter() {
                                            @Override
                                            public void onCompleteMainThread() {
                                                super.onCompleteMainThread();
                                                dialog.dismiss();
                                                Logger.d("onCompleteMainThread");
                                            }
                                        });
                            }
                        });
            }
        });
    }
}
