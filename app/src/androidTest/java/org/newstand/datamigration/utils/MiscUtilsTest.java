package org.newstand.datamigration.utils;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderSource;

/**
 * Created by Nick@NewStand.org on 2017/4/6 14:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class MiscUtilsTest {
    @Test
    public void installApkByIntent() throws Exception {

        AppRecord record = (AppRecord) DataLoaderManager.from(InstrumentationRegistry.getTargetContext())
                .load(LoaderSource.builder().parent(LoaderSource.Parent.Android).build(), DataCategory.App).iterator().next();


        MiscUtils.installApkByIntent(InstrumentationRegistry.getTargetContext(), record.getPath());
    }
}