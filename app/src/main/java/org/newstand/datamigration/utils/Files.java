package org.newstand.datamigration.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.FileProvider;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.logger.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Nick@NewStand.org on 2017/3/13 10:03
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class Files {

    /**
     * Interface definition for a callback to be invoked regularly as
     * verification proceeds.
     */
    public interface ProgressListener {
        /**
         * Called periodically as the verification progresses.
         *
         * @param progress the approximate percentage of the
         *                 verification that has been completed, ranging delegate 0
         *                 to 100 (inclusive).
         */
        public void onProgress(float progress);
    }


    public static void copy(String spath, String dpath, @Nullable ProgressListener listener) throws IOException {
        FileInputStream fis = new FileInputStream(spath);
        FileOutputStream fos = new FileOutputStream(dpath);
        int totalByte = fis.available();
        int read = 0;
        int n;
        byte[] buffer = new byte[4096];
        while ((n = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, n);
            fos.flush();
            read += n;
            float per = (float) read / (float) totalByte;
            if (listener != null) {
                listener.onProgress(per * 100);
            }
        }
        Closer.closeQuietly(fis);
        Closer.closeQuietly(fos);
    }

    public static String formatSize(long fileSize) {
        String wellFormatSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            wellFormatSize = fileSize + "B";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / 1024) + "KB";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / (1024 * 1024)) + "MB";
        } else if (fileSize >= (1024 * 1024 * 1024)) {
            wellFormatSize = Long.toString(fileSize / (1024 * 1024 * 1024)) + "GB";
        }
        return wellFormatSize;
    }

    public static boolean deleteDir(File dir) {
        final boolean[] res = {true};
        Collections.consumeRemaining(com.google.common.io.Files.fileTreeTraverser()
                .postOrderTraversal(dir), new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                if (!file.delete()) res[0] = false;
            }
        });
        return res[0];
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "org.newstand.datamigration.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void shareDateMigrationAsync(final Context context) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                shareDateMigration(context);
            }
        });
    }

    public static void shareDateMigration(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            String path = info.publicSourceDir;
            shareApk(context, path);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("App not found~");
        }
    }

    public static void shareApk(Context context, String file) {
        try {
            String toPlace = SettingsProvider.getCommonDataDir()
                    + File.separator
                    + "DataMigration@"
                    + VersionRetriever.currentVersionName()
                    + ".jpeg";
            if (!new File(toPlace).exists())
                com.google.common.io.Files.copy(new File(file), new File(toPlace));
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, getUriForFile(context, new File(toPlace)));
            share.setType("image/jpeg");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(share, context.getString(R.string.title_transport_share)));
        } catch (IOException e) {
            Logger.e(e, "Fail to copy file");
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public static boolean writeString(String str, String path) {
        BufferedWriter bf = null;
        try {
            com.google.common.io.Files.createParentDirs(new File(path));
            bf = com.google.common.io.Files.newWriter(new File(path), Charset.defaultCharset());
            bf.write(str, 0, str.length());
            return true;
        } catch (FileNotFoundException e) {
            Logger.e(e, "Fail to write file %s", path);
        } catch (IOException e) {
            Logger.e(e, "Fail to write file %s", path);
        } finally {
            Closer.closeQuietly(bf);
        }
        return false;
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    @Nullable
    @WorkerThread
    public static String readString(String path) {
        BufferedReader reader = null;
        try {
            if (!new File(path).exists())
                return null;
            reader = com.google.common.io.Files.newReader(new File(path), Charset.defaultCharset());
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Logger.e(e, "Fail to read file %s", path);
        } catch (IOException e) {
            Logger.e(e, "Fail to read file %s", path);
        } finally {
            Closer.closeQuietly(reader);
        }
        return null;
    }

    public static boolean isEmptyDir(File dir) {
        return dir.exists() && dir.isDirectory() && dir.list().length == 0;
    }

    public static void moveAsync(final File from, final File to,
                                 final ActionListener<Boolean> listener) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    com.google.common.io.Files.move(from, to);
                    listener.onAction(true);
                } catch (IOException e) {
                    Logger.e(e, "Fail to move delegate %s to %s", from, to);
                    listener.onAction(false);
                }
            }
        };
        SharedExecutor.execute(r);
    }
}
