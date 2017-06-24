package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.data.model.DataCategory;

/**
 * Created by Nick@NewStand.org on 2017/4/7 15:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupSystemAppListFragment extends BackupAppListFragment {
    @Override
    DataCategory getDataType() {
        return DataCategory.SystemApp;
    }
}
