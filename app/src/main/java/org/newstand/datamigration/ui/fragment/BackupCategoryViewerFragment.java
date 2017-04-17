package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.R;

/**
 * Created by Nick@NewStand.org on 2017/3/7 18:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupCategoryViewerFragment extends CategoryViewerFragment {
    @Override
    protected int getFabIntro() {
        return R.string.fab_intro_category_restore;
    }
}
