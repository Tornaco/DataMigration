package org.newstand.datamigration.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/28 12:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ApkUtilTest {
    @Test
    public void loadNameByPkgName() throws Exception {
        Logger.d("Name %s", ApkUtil.loadNameByPkgName(InstrumentationRegistry.getTargetContext(), "com.tencent.mm"));
    }

    @Test
    public void loadIconByPkgName() throws Exception {

    }

    @Test
    public void loadIconByFilePath() throws Exception {

    }

    @Test
    public void loadVersionByFilePath() throws Exception {

    }

    @Test
    public void loadPkgNameByFilePath() throws Exception {

    }

    @Test
    public void loadAppNameByFilePath() throws Exception {

    }

}