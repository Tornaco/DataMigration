package org.newstand.datamigration.utils;

import org.junit.Test;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/5/3 11:17
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class FilesTest {
    @Test
    public void isEmptyDir() throws Exception {
        Logger.d(Files.isEmptyDir(new File(SettingsProvider.getDataMigrationRootDir())));
        Logger.d(Files.isEmptyDir(new File(SettingsProvider.getTestDir() + "/EM")));
        com.google.common.io.Files.createParentDirs(new File(SettingsProvider.getTestDir() + "/EM"));
        File dir = new File(SettingsProvider.getTestDir() + "/EM");
        Logger.d(dir.exists());
        Logger.d(Arrays.toString(dir.list()));
        dir.delete();
        dir.mkdir();
        Logger.d(Files.isEmptyDir(new File(SettingsProvider.getTestDir() + "/EM")));
    }

}