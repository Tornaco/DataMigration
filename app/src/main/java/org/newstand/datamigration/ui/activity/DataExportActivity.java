package org.newstand.datamigration.ui.activity;

import android.support.v4.app.Fragment;

import org.newstand.datamigration.ui.fragment.DataExportManageFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:36
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataExportActivity extends DataTransportActivity {
    @Override
    protected Fragment getTransportFragment() {
        return new DataExportManageFragment();
    }
}
