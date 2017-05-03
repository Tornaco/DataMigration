package org.newstand.datamigration.provider;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/3/31 15:27
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsProviderTest {
    @Test
    public void getCommonRootDir() throws Exception {

        String root = SettingsProvider.getDataMigrationRootDir();

        SettingsProvider.setDataMigrationRootDir(Environment.getExternalStorageDirectory().getPath()
                + "TestDataMDir");

        StringBuilder sb = new StringBuilder();
        sb.append(SettingsProvider.getDataMigrationRootDir()).append("\n");
        sb.append(SettingsProvider.getBackupRootDir()).append("\n");
        sb.append(SettingsProvider.getReceivedRootDir()).append("\n");
        sb.append(SettingsProvider.getTestDir()).append("\n");
        sb.append(SettingsProvider.getLogDir()).append("\n");
        sb.append(SettingsProvider.getLicenseRootDir()).append("\n");
        sb.append(SettingsProvider.getReceivedDirByCategory(DataCategory.Wifi, Session.create())).append("\n");
        sb.append(SettingsProvider.getReceivedRootDir()).append("\n");
        sb.append(SettingsProvider.getRecSessionDir(Session.create())).append("\n");
        sb.append(SettingsProvider.getAppDataDir()).append("\n");
        sb.append(SettingsProvider.getBackupAppApkDirName()).append("\n");
        sb.append(SettingsProvider.getBackupAppDataDirName()).append("\n");
        sb.append(SettingsProvider.getBackupDirByCategory(DataCategory.Wifi, Session.from("Test"))).append("\n");
        sb.append(SettingsProvider.getBackupSessionDir(Session.from("Test2"))).append("\n");
        sb.append(SettingsProvider.getBackupSessionInfoPath(Session.create())).append("\n");

        Logger.d(sb.toString());

        SettingsProvider.setDataMigrationRootDir(root);
    }

    @Test
    public void getEncryptPath() throws Exception {
        String testPath = Environment.getExternalStorageDirectory().getPath() + "/test2222";
        String encrypt = SettingsProvider.getEncryptPath(testPath);
        Logger.d("Enc: %s", encrypt);
    }

    @Test
    public void getDecryptPath() throws Exception {
        String testPath = Environment.getExternalStorageDirectory().getPath() + "/test2222";
        String encrypt = SettingsProvider.getEncryptPath(testPath);
        Logger.d("Enc: %s", encrypt);

        String re = SettingsProvider.getDecryptPath(encrypt);

        Logger.d("DeEnc: %s", re);
    }

    @Test
    public void isUserNoticed() throws Exception {

    }

    @Test
    public void setUserNoticed() throws Exception {

    }

    @Test
    public void getHelpMdFilePath() throws Exception {

    }

    @Test
    public void getDefHelpFileAssetsPath() throws Exception {

    }

    @Test
    public void getCommonDataDir() throws Exception {

    }

    @Test
    public void setDonateQrPath() throws Exception {

    }

    @Test
    public void getDonateQrPathChecked() throws Exception {

    }

    @Test
    public void getLastUpdateCheckTime() throws Exception {

    }

    @Test
    public void setLastUpdateCheckTime() throws Exception {

    }

    @Test
    public void shouldCheckForUpdateNow() throws Exception {

    }

    @Test
    public void isTipsNoticed() throws Exception {

    }

    @Test
    public void setTipsNoticed() throws Exception {

    }

    @Test
    public void isBugReportEnabled() throws Exception {

    }

    @Test
    public void setBugReportEnabled() throws Exception {

    }

    @Test
    public void setAutoInstallAppEnabled() throws Exception {

    }

    @Test
    public void isAutoInstallAppEnabled() throws Exception {

    }

    @Test
    public void setInstallData() throws Exception {

    }

    @Test
    public void isInstallDataEnabled() throws Exception {

    }

    @Test
    public void getThemeColor() throws Exception {

    }

    @Test
    public void setAppThemeColor() throws Exception {

    }

    @Test
    public void isUnderTest() throws Exception {

    }

    @Test
    public void setUnderTest() throws Exception {

    }

    @Test
    public void init() throws Exception {

    }

    @Test
    public void readBoolean() throws Exception {

    }

    @Test
    public void readBoolean1() throws Exception {

    }

    @Test
    public void writeBoolean() throws Exception {

    }

    @Test
    public void readString() throws Exception {

    }

    @Test
    public void readString1() throws Exception {

    }

    @Test
    public void writeString() throws Exception {

    }

    @Test
    public void getBackupRootDir() throws Exception {

    }

    @Test
    public void getReceivedRootDir() throws Exception {

    }

    @Test
    public void getBackupSessionAssetFile() throws Exception {

    }

    @Test
    public void getBackupSessionDir() throws Exception {

    }

    @Test
    public void getBackupSessionInfoPath() throws Exception {

    }

    @Test
    public void getBackupSessionInfoFileName() throws Exception {

    }

    @Test
    public void getRecSessionDir() throws Exception {

    }

    @Test
    public void getLogDir() throws Exception {

    }

    @Test
    public void getTestDir() throws Exception {

    }

    @Test
    public void getBackupDirByCategory() throws Exception {

    }

    @Test
    public void getReceivedDirByCategory() throws Exception {

    }

    @Test
    public void getRestoreDirByCategory() throws Exception {

    }

    @Test
    public void getWFDDeviceNamePrefix() throws Exception {

    }

    @Test
    public void getDeviceName() throws Exception {

    }

    @Test
    public void setDeviceName() throws Exception {

    }

    @Test
    public void isAutoConnectEnabled() throws Exception {

    }

    @Test
    public void setAutoConnectEnabled() throws Exception {

    }

    @Test
    public void isTransitionAnimationEnabled() throws Exception {

    }

    @Test
    public void setTransitionAnimationEnabled() throws Exception {

    }

    @Test
    public void getWorkMode() throws Exception {

    }

    @Test
    public void setWorkMode() throws Exception {

    }

    @Test
    public void getDiscoveryTimeout() throws Exception {

    }

    @Test
    public void getRequestConnectioninfoTimeout() throws Exception {

    }

    @Test
    public void observe() throws Exception {

    }

    @Test
    public void unObserve() throws Exception {

    }

    @Test
    public void getAppDataDir() throws Exception {

    }

    @Test
    public void getBackupAppDataDirName() throws Exception {

    }

    @Test
    public void getBackupAppApkDirName() throws Exception {

    }

    @Test
    public void getLicenseRootDir() throws Exception {

    }

    @Test
    public void setDebugEnabled() throws Exception {

    }

    @Test
    public void isDebugEnabled() throws Exception {

    }

    @Test
    public void getDefSmsApp() throws Exception {

    }

    @Test
    public void setDefSmsApp() throws Exception {

    }

    @Test
    public void getBackupInterval() throws Exception {
        Interval interval = SettingsProvider.getBackupInterval();
        Logger.d(interval);
        Logger.d(interval.getIntervalMills());
    }

    @Test
    public void setBackupInterval() throws Exception {
        SettingsProvider.setBackupInterval(Interval.Minutes);
        getBackupInterval();
    }

    @Test
    public void getTransportServerPorts() throws Exception {
        int[] ports = SettingsProvider.getTransportServerPorts();
        Logger.d(Arrays.toString(ports));
        Assert.assertTrue(ports.length > 0);
    }

}