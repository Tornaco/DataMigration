package org.newstand.datamigration.service.schedule;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.utils.Files;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListenerAdapter;
import org.newstand.datamigration.worker.transport.backup.DataBackupManager;
import org.newstand.logger.Logger;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 18:02
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupActionExecutor implements ActionExecutor<BackupActionSettings>, ContextWireable {
    @Getter
    @Setter
    private Context context;

    @Override
    public int execute(final BackupActionSettings settings) {

        onPreExecute(settings.getSession());

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull final DataCategory dataCategory) {
                if (!settings.getDataCategories().contains(dataCategory)) {
                    return;
                }
                // Perform backup~
                DataBackupManager.from(context, settings.getSession())
                        .performBackup(new TransportListenerAdapter() {
                            @Override
                            public void onRecordFail(DataRecord record, Throwable err) {
                                super.onRecordFail(record, err);
                                Logger.e(err, "onRecordFail in BackupActionExecutor %s", record);
                            }
                        }, DataLoaderManager.from(context).load((LoaderSource.builder()
                                .parent(LoaderSource.Parent.Android).build()), dataCategory), dataCategory);

            }
        });

        onExecuted(settings.getSession());

        return 0;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    private void onPreExecute(Session session) {
        // Delete old session.
        BKSessionRepoService.get().delete(context, session);
        Files.deleteDir(new File(SettingsProvider.getBackupSessionDir(session)));
    }

    private void onExecuted(Session session) {
        Logger.d("Scheduled backup executed~ %s", session);
        // Update session date.
        session.setDate(System.currentTimeMillis());
        BKSessionRepoService.get().insert(getContext(), session);
    }
}
