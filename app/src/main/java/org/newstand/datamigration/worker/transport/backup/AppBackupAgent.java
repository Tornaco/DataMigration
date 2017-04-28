package org.newstand.datamigration.worker.transport.backup;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.MiscUtils;
import org.newstand.datamigration.utils.RootTools2;
import org.newstand.datamigration.utils.SeLinuxContextChanger;
import org.newstand.datamigration.utils.Zipper;
import org.newstand.logger.Logger;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/12 21:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class AppBackupAgent implements BackupAgent<AppBackupSettings, AppRestoreSettings>,
        ContextWireable {

    private FileBackupAgent fileBackupAgent;
    @Getter
    @Setter
    private Context context;

    AppBackupAgent() {
        fileBackupAgent = new FileBackupAgent();
    }

    @Override
    public Res backup(final AppBackupSettings backupSettings) throws Exception {

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

        final Res[] res = {Res.OK};

        if (SettingsProvider.isInstallDataEnabled()) {
            boolean hasRoot = RootManager.getInstance().obtainPermission();
            if (!hasRoot) {
                Logger.e("Fail to obtain root~");
                return new RootMissingException();
            }

            // Data source
            String appDataDir = backupSettings.getSourceDataPath();
            // Data dest
            String destination = backupSettings.getDestDataPath();

            Logger.d("Saving data from %s, to %s", appDataDir, destination);

            boolean cr = Zipper.compressTar(destination, appDataDir);

            Logger.d("Saving data res %s", cr);

            if (!cr) {
                res[0] = new CompressErr();
                return res[0];
            }

            // ExtraData
            final String[] extraDataDirs = backupSettings.getExtraDirs();
            if (extraDataDirs != null) {
                Collections.consumeRemaining(extraDataDirs, new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) {
                        File extraDir = new File(s);
                        if (extraDir.exists()) {
                            String destExtraPath = backupSettings.getDestExtraDataPath()
                                    + File.separator
                                    + Files.getNameWithoutExtension(s)
                                    + ".tar.gz";

                            boolean b = Zipper.compressTar(destExtraPath, extraDir.getPath());
                            Logger.d("Saving extra data res %s", b);
                            if (!b) {
                                res[0] = new CompressErr();
                            }
                        }
                    }
                });
            }
        }

        return res[0];
    }

    @Override
    public Res restore(AppRestoreSettings restoreSettings) throws Exception {

        Logger.d("restore with settings %s", restoreSettings);

        PackageInstallReceiver installReceiver = new PackageInstallReceiver(restoreSettings.getAppRecord().getPkgName());
        installReceiver.register(getContext());

        boolean autoInstall = SettingsProvider.isAutoInstallAppEnabled();

        if (!autoInstall || !installAppWithRoot(restoreSettings)) {
            installAppWithIntent(restoreSettings);
        }

        installReceiver.waitUtilInstalled();
        Sleeper.sleepQuietly(1000); // Sleep for 1s to let user dismiss the install page...Maybe there is a better way?
        installReceiver.unRegister(getContext());

        boolean installData = SettingsProvider.isInstallDataEnabled();

        Res res = Res.OK;

        if (installData) {
            res = installData(restoreSettings);

            if (res == Res.OK) {
                res = installExtraData(restoreSettings);
            }
        }

        return res;
    }

    private boolean installAppWithRoot(AppRestoreSettings restoreSettings) {
        boolean hasRoot = RootManager.getInstance().obtainPermission();
        if (!hasRoot) {
            Logger.e("Fail to obtain root~");
            return false;
        }
        Result installRes = RootManager.getInstance().installPackage(restoreSettings.getSourceApkPath());
        MiscUtils.printResult("InstallApk", installRes);

        if (!installRes.getResult()) {
            Logger.e("Fail to install app with root");
            return false;
        }

        return true;
    }

    private boolean installAppWithIntent(AppRestoreSettings restoreSettings) {
        MiscUtils.installApkByIntent(getContext(), restoreSettings.getSourceApkPath());
        return true;
    }

    private Res installData(AppRestoreSettings restoreSettings) throws Exception {
        // Install data
        String dataFromPath = restoreSettings.getSourceDataPath();
        String dataToPath = restoreSettings.getDestDataPath();

        Logger.d("Install data from %s, to %s", dataFromPath, dataToPath);

        boolean res = Zipper.deCompressTar(dataFromPath);

        Logger.d("Install data res %s", res);

        // Restore selinux context
        if (!SeLinuxContextChanger.restoreContext(dataToPath)) {
            Res re = new SeLinuxModeChangeErr();
            Logger.e(re, "Fail to change mode for %s", dataToPath);
            return re;
        }

        int uid = getUID(getContext(), restoreSettings.getAppRecord().getPkgName());
        Logger.d("Target uid %d", uid);

        // Change owner and group.
        if (!RootTools2.changeOwner(restoreSettings.getDestDataPath(), uid)
                || !RootTools2.changeGroup(restoreSettings.getDestDataPath(), uid)) {
            return new OnwerGroupChangeFailException("Fail to change owner/group of "
                    + restoreSettings.getDestDataPath() + " to" + uid);
        }

        return Res.OK;
    }

    private Res installExtraData(AppRestoreSettings restoreSettings) throws Exception {
        String extraSourceDir = restoreSettings.getExtraSourceDataPath();
        File dir = new File(extraSourceDir);
        Iterable<File> sunFiles = Files.fileTreeTraverser().children(dir);
        final Res[] res = {Res.OK};
        if (sunFiles != null) {
            Collections.consumeRemaining(sunFiles, new Consumer<File>() {
                @Override
                public void accept(@NonNull File file) {
                    boolean b = Zipper.deCompressTar(file.getPath());
                    Logger.d("Decompress extra files, res %s", b);
                    res[0] = new DeCompressErr();
                }
            });
        }
        return res[0];
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