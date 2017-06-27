package org.newstand.datamigration.loader.impl;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.ApkUtil;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/12 20:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AppLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            packages = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        } else {
            packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        }

        for (PackageInfo packageInfo : packages) {

            AppRecord appRecord = new AppRecord();
            appRecord.setDisplayName(packageInfo.applicationInfo.loadLabel(pm).toString());
            appRecord.setPkgName(packageInfo.packageName);
            appRecord.setPath(packageInfo.applicationInfo.publicSourceDir);

            // Ignore our self.
            if (getContext().getPackageName().equals(appRecord.getPkgName())) {
                continue;
            }

            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            if (ignoreSystemApp() && isSystemApp) {
                continue;
            }

            if (!isSystemApp && ignoreUserApp()) {
                continue;
            }

            try {
                long size = Files.asByteSource(new File(appRecord.getPath())).size();
                appRecord.setSize(size);
            } catch (IOException e) {
                Logger.e("Failed to query size for:%s", appRecord);
            }

            appRecord.setVersionName(packageInfo.versionName);

            records.add(appRecord);
        }

        return records;
    }

    protected boolean ignoreSystemApp() {
        return true;
    }

    protected boolean ignoreUserApp() {
        return false;
    }

    protected DataCategory getDateCategory() {
        return DataCategory.App;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        String dir =
                source.getParent() == LoaderSource.Parent.Received ?
                        SettingsProvider.getReceivedDirByCategory(getDateCategory(), session)
                        : SettingsProvider.getBackupDirByCategory(getDateCategory(), session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {

                AppRecord record = new AppRecord();
                record.setDisplayName(Files.getNameWithoutExtension(file.getPath()));
                record.setPath(file.getPath() + File.separator + SettingsProvider.getBackupAppApkDirName()
                        + File.separator + record.getDisplayName() + AppRecord.APK_FILE_PREFIX);

                boolean apkExist = new File(record.getPath()).exists();
                record.setHasApk(apkExist);

                boolean dataExist = new File(file.getPath() + File.separator + SettingsProvider.getBackupAppDataDirName())
                        .exists();
                record.setHasData(dataExist);
                boolean extraDataExist = new File(file.getPath() + File.separator + SettingsProvider.getBackupExtraDataDirName())
                        .exists();
                record.setHasExtraData(extraDataExist);
                if (apkExist) {
                    try {
                        String packageName = ApkUtil.loadPkgNameByFilePath(getContext(), record.getPath());
                        if (TextUtils.isEmpty(packageName)) {
                            Logger.w("Ignore app while package name is null %s", file.getPath());
                            return;
                        }
                        Drawable icon = ApkUtil.loadIconByFilePath(getContext(), record.getPath());
                        record.setVersionName(ApkUtil.loadVersionByFilePath(getContext(), record.getPath()));
                        record.setPkgName(packageName);
                        record.setSize(Files.asByteSource(new File(record.getPath())).size());
                        String appName = ApkUtil.loadAppNameByFilePath(getContext(), record.getPath());
                        Logger.v("appName %s", appName);
                        record.setDisplayName(file.getName());

                        record.setHandleApk(false);
                        record.setHandleData(false);

                        records.add(record);
                    } catch (Throwable e) {
                        Logger.e(e, "Failed to query size for %s", record);
                    }
                } else {
                    // Find app record info.
                    String jsonPath = file.getPath() + File.separator + SettingsProvider.getBackupAppApkDirName()
                            + File.separator + record.getDisplayName() + AppRecord.APK_META_PREFIX;
                    File jsonFile = new File(jsonPath);
                    if (jsonFile.exists()) try {
                        AppRecord jsonRecord = AppRecord.fromJson(org.newstand.datamigration.utils.Files.readString(jsonPath));

                        record.setVersionName(jsonRecord.getVersionName());
                        record.setPkgName(jsonRecord.getPkgName());
                        record.setSize(0);
                        record.setDisplayName(jsonRecord.getDisplayName());
                        record.setHandleApk(false);
                        record.setHandleData(false);

                        records.add(record);
                    } catch (Throwable e) {
                        Logger.e(e, "Failed to parse app meta in %s", jsonPath);
                    }
                }
            }
        });

        return records;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
