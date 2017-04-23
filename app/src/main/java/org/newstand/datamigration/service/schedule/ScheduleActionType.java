package org.newstand.datamigration.service.schedule;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.ResBinder;
import org.newstand.datamigration.worker.transport.Session;

import java.util.ArrayList;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum ScheduleActionType implements ActionExecutorProducer, ResBinder {
    Backup {
        @Override
        public int nameRes() {
            return R.string.title_backup;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_backup;
        }

        @Override
        public BackupActionSettings createEmptySettings() {
            return BackupActionSettings.builder()
                    .dataCategories(new ArrayList<DataCategory>())
                    .session(Session.create())
                    .build();
        }

        @Override
        public ActionExecutor produce() {
            return new BackupActionExecutor();
        }
    },
}
