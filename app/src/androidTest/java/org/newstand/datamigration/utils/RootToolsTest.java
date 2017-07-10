package org.newstand.datamigration.utils;

import android.support.test.runner.AndroidJUnit4;

import com.stericson.rootools.RootTools;
import com.stericson.rootshell.exceptions.RootDeniedException;
import com.stericson.rootshell.execution.Shell;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nick@NewStand.org on 2017/4/1 16:18
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class RootToolsTest {

    @Before
    public void requireRoot() throws TimeoutException, RootDeniedException, IOException {
        Shell shell = RootTools.getShell(true);
        Logger.d("Shell closed %s", shell.isClosed);
    }

    @Test
    public void testShellCommand() {
        boolean root = RootTools.isRootAvailable();
        Logger.d("Root available %s", root);
    }

    @Test
    public void testQQ() {
        String qqDataDirPath = "data/data/com.tencent.mobileqq/";

        Logger.w("Exist %s", RootTools.exists(qqDataDirPath, true));
        Logger.w("Exist %s", RootTools.exists(qqDataDirPath + "shared_prefs/1163397166_far.xml", false));

        Logger.d("copy %s", RootTools.copyFile(qqDataDirPath + "shared_prefs/1163397166_far.xml", SettingsProvider.getTestDir() + "/qq.xml", false, false));

        // copy back!
        RootTools.copyFile(SettingsProvider.getTestDir() + "/com.tencent.mobileqq", qqDataDirPath, false, false);

    }
}
