package org.newstand.datamigration.worker.backup;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.stericson.rootools.RootTools;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.strategy.WorkMode;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.MiscUtils;
import org.newstand.datamigration.utils.RootTools2;
import org.newstand.logger.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/12 21:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class AppBackupAgent implements BackupAgent<AppBackupSettings, AppRestoreSettings>, ContextWireable {

    private FileBackupAgent fileBackupAgent;
    @Getter
    @Setter
    private Context context;

    AppBackupAgent() {
        fileBackupAgent = new FileBackupAgent();
    }

    @Override
    public Res backup(AppBackupSettings backupSettings) throws Exception {

        Logger.d("backup with settings %s", backupSettings);

        // Apk source
        String apkPath = backupSettings.getSourceApkPath();
        // Apk dest
        String destApkPath = backupSettings.getDestApkPath();

        // Apk backup go~
        FileBackupSettings apkSettings = new FileBackupSettings();
        apkSettings.setSourcePath(apkPath);
        apkSettings.setDestPath(destApkPath);
        Res apkRes = fileBackupAgent.backup(apkSettings);

        if (!Res.isOk(apkRes)) {
            return apkRes;
        }

        // Check work mode and root.
        WorkMode workMode = SettingsProvider.workMode();
        if (workMode == WorkMode.ROOT) {
            boolean hasRoot = RootManager.getInstance().obtainPermission();
            if (!hasRoot) {
                Logger.e("Fail to obtain root~");
                return new RootMissingException();
            }

            // Data source
            String appDataDir = backupSettings.getSourceDataPath();
            // Debug
            MiscUtils.printResult("ls -l dataDir", RootManager.getInstance().runCommand("ls -l " + appDataDir));
            // Data dest
            String destination = backupSettings.getDestDataPath();

            Logger.d("Copying data from %s, to %s", appDataDir, destination);

            boolean res = RootTools.copyFile(appDataDir, destination, true, true);

            Logger.d("Copy data res %s", res);
        }

        return Res.OK;
    }

    @Override
    public Res restore(AppRestoreSettings restoreSettings) throws Exception {

        Logger.d("restore with settings %s", restoreSettings);

        PackageInstallReceiver installReceiver = new PackageInstallReceiver(restoreSettings.getAppRecord().getPkgName());
        installReceiver.register(getContext());

        WorkMode workMode = SettingsProvider.workMode();

        // Install apk
        if (workMode == WorkMode.ROOT) {
            boolean hasRoot = RootManager.getInstance().obtainPermission();
            if (!hasRoot) {
                Logger.e("Fail to obtain root~");
                return new RootMissingException();
            }

            Result installRes = RootManager.getInstance().installPackage(restoreSettings.getSourceApkPath());
            MiscUtils.printResult("InstallApk", installRes);

            if (!installRes.getResult()) {
                Logger.e("Fail to install app, wont install data.");
                return new ApkInstallFailException();
            }
        } else {
            MiscUtils.installApkByIntent(getContext(), restoreSettings.getSourceApkPath());
        }

        installReceiver.waitUtilInstalled();
        Sleeper.sleepQuietly(1000); // Sleep for 1s to let user dismiss the install page...Maybe there is a better way?
        installReceiver.unRegister(getContext());

        if (workMode == WorkMode.ROOT) {
            // Install data
            String dataFromPath = restoreSettings.getSourceDataPath();
            String dataToPath = restoreSettings.getDestDataPath();

            Logger.d("Copying data from %s, to %s", dataFromPath, dataToPath);

            boolean res = RootTools.copyFile(dataFromPath, dataToPath, true, true);

            Logger.d("Copy data res %s", res);

            int uid = getUID(getContext(), restoreSettings.getAppRecord().getPkgName());
            Logger.d("Target uid %d", uid);

            // Change owner and group.
            if (!RootTools2.changeOwner(restoreSettings.getDestDataPath(), uid)
                    || !RootTools2.changeGroup(restoreSettings.getDestDataPath(), uid)) {
                return new OnwerGroupChangeFailException("Fail to change owner/group of " + restoreSettings.getDestDataPath() + " to" + uid);
            }
        }

        return Res.OK;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    public static int getUID(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES);
        } else {
            applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
        }
        return applicationInfo.uid;
    }
}
