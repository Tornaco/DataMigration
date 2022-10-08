package org.newstand.datamigration.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

/**
 * Created by Nick@NewStand.org on 2017/3/13 9:56
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class ApkUtil {

    public static CharSequence loadNameByPkgName(@NonNull Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info == null) return null;
            return info.loadLabel(pm);
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    public static Drawable loadIconByPkgName(@NonNull Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info == null) return null;
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    public static Drawable loadIconByFilePath(@NonNull Context context, String filePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null; // FIX NPE
        ApplicationInfo appInfo = info.applicationInfo;
        appInfo.sourceDir = filePath;
        appInfo.publicSourceDir = filePath;
        return appInfo.loadIcon(pm);
    }

    public static String loadVersionByFilePath(@NonNull Context context, String filePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null; // FIX NPE
        return info.versionName;
    }

    public static String loadPkgNameByFilePath(@NonNull Context context, String filePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null; // FIX NPE
        return info.packageName;
    }

    public static String loadAppNameByFilePath(@NonNull Context context, String filePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null; // FIX NPE
        return info.applicationInfo.name;
    }
}
