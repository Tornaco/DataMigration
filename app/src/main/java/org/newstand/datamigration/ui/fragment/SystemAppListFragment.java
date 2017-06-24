package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.data.model.DataCategory;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SystemAppListFragment extends AppListFragment {
    @Override
    DataCategory getDataType() {
        return DataCategory.SystemApp;
    }
}
