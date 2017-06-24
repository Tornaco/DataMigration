package org.newstand.datamigration.loader.impl;

import org.newstand.datamigration.data.model.DataCategory;

/**
 * Created by Nick@NewStand.org on 2017/3/12 20:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SystemAppLoader extends AppLoader {

    @Override
    protected boolean ignoreSystemApp() {
        return false;
    }

    @Override
    protected boolean ignoreUserApp() {
        return true;
    }

    @Override
    protected DataCategory getDateCategory() {
        return DataCategory.SystemApp;
    }
}
