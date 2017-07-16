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
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.MiscUtils;
import org.newstand.datamigration.utils.RootTarUtil;
import org.newstand.datamigration.utils.RootTools2;
import org.newstand.datamigration.utils.SeLinuxContextChanger;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/12 21:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class AppBackupAgent extends ProgressableBackupAgent<AppBackupSettings, AppRestoreSettings> implements ContextWireable {

    @Getter
    @Setter
    private Context context;

    AppBackupAgent() {
    }

    @Override
    public Res backup(final AppBackupSettings backupSettings) throws Exception {

        Logger.d("backup with settings %s", backupSettings);

        // Write meta info.
        String metaPath = backupSettings.getDestMetaPath();
        if (!org.newstand.datamigration.utils.Files.writeString(backupSettings.getAppRecord().toJson(), metaPath)) {
            Logger.e("Fail to write app meta~");// We are still ok.
        }

        if (backupSettings.isBackupApp()) {
            // Apk source
            String apkPath = backupSettings.getSourceApkPath();
            // Apk dest
            String destApkPath = backupSettings.getDestApkPath();

            // Publish progress.
            org.newstand.datamigration.utils.Files.copy(apkPath, destApkPath,
                    new org.newstand.datamigration.utils.Files.ProgressListener() {
                        @Override
                        public void onProgress(float progress) {
                            getProgressListener().onProgress(RecordEvent.CopyApk, progress);
                        }
                    });
        }

        final Res[] res = {Res.OK};

        if (backupSettings.isBackupData()) {
            boolean hasRoot = RootManager.getInstance().obtainPermission();
            if (!hasRoot) {
                Logger.e("Fail to obtain root~");
                return new RootMissingException();
            }

            // Data source
            String appDataDir = backupSettings.getSourceDataPath();
            // Data dest
            String destination = backupSettings.getDestDataPath();

            Logger.d("Saving data delegate %s, to %s", appDataDir, destination);

            // Publish progress.
            getProgressListener().onProgress(RecordEvent.CopyData, 0);

            boolean cr = RootTarUtil.compressTar(destination, appDataDir);

            Logger.d("Saving data res %s", cr);

            if (!cr) {
                res[0] = new CompressErr();
                return res[0];
            }

            // Publish progress.
            getProgressListener().onProgress(RecordEvent.CopyData, 100);

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

                            boolean b = RootTarUtil.compressTar(destExtraPath, extraDir.getPath());
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

        if (restoreSettings.isInstallApp()) {

            // Prevent our app killed by installer.
            if (getContext().getPackageName().equals(restoreSettings.getAppRecord().getPkgName())) {
                return new OperationNotAllowedErr();
            }

            Logger.d("Installing apk");

            // Publish progress.
            getProgressListener().onProgress(RecordEvent.InstallApk, 0);

            PackageInstallReceiver installReceiver = new PackageInstallReceiver(restoreSettings.getAppRecord().getPkgName());
            installReceiver.register(getContext());

            boolean autoInstall = SettingsProvider.isAutoInstallAppEnabled();

            if (!autoInstall || !installAppWithRoot(restoreSettings)) {
                installAppWithIntent(restoreSettings);
            }

            try {
                if (!installReceiver.waitUtilInstalled(getProgressListener())) {
                    Logger.e("Timeout waiting for apk installer");
                    return new ApkInstallFailException();
                }
            } finally {
                installReceiver.unRegister(getContext());
            }

            Sleeper.sleepQuietly(2000); // Sleep for 1s to let user dismiss the install page...Maybe there is a better way?

            // Publish progress.
            getProgressListener().onProgress(RecordEvent.InstallApk, 100);
        }

        boolean installData = restoreSettings.isInstallData();

        Res res = Res.OK;

        if (installData) {

            // Publish progress.
            getProgressListener().onProgress(RecordEvent.InstallData, 0);
            res = installData(restoreSettings);
            getProgressListener().onProgress(RecordEvent.InstallData, 100);

            if (res == Res.OK) {

                // Publish progress.
                getProgressListener().onProgress(RecordEvent.InstallExtraData, 0);
                res = installExtraData(restoreSettings);
                getProgressListener().onProgress(RecordEvent.InstallExtraData, 100);
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

        String apkPath = restoreSettings.getSourceApkPath();
        String tmpPath = SettingsProvider.getAppInstallerCacheRootDir()
                + File.separator
                + UUID.randomUUID().toString();
        // Check if this path has any space.
        if (apkPath.contains(" ")) {
            Logger.w("This apk path contains invalid char, replacing...");
            // copy a tmp.
            try {
                Files.createParentDirs(new File(tmpPath));
                Files.copy(new File(apkPath), new File(tmpPath));
                apkPath = tmpPath;
                Logger.d("Now using new apk path:%s", apkPath);
            } catch (IOException e) {
                Logger.e("Fail copy apk");
            }
        }

        Result installRes = RootManager.getInstance().installPackage(apkPath);
        MiscUtils.printResult("InstallApk", installRes);

        if (!installRes.getResult()) {
            Logger.e("Fail to install app with root");
            return false;
        }

        // Clean up.
        BlackHole.eat(new File(tmpPath).exists() && new File(tmpPath).delete());

        return true;
    }

    private boolean installAppWithIntent(AppRestoreSettings restoreSettings) {
        MiscUtils.installApkByIntent(getContext(), restoreSettings.getSourceApkPath());
        return true;
    }

    private Res installData(AppRestoreSettings restoreSettings) throws Exception {
        if (!RootManager.getInstance().obtainPermission()) {
            Logger.d("Fail to obtain root permission");
            return new RootMissingException();
        }

        // Kill if app is running~
        RootManager.getInstance().killProcessByName(restoreSettings.getAppRecord().getPkgName());

        // Install data
        String dataFromPath = restoreSettings.getSourceDataPath();
        String dataToPath = restoreSettings.getDestDataPath();

        Logger.d("Install data delegate %s, to %s", dataFromPath, dataToPath);

        boolean res = RootTarUtil.deCompressTar(dataFromPath);

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
                    boolean b = RootTarUtil.deCompressTar(file.getPath());
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