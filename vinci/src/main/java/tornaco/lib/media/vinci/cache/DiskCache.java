package tornaco.lib.media.vinci.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.AtomicFile;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import lombok.Getter;
import tornaco.lib.media.vinci.Enforcer;
import tornaco.lib.media.vinci.ErrorReporter;
import tornaco.lib.media.vinci.utils.BitmapUtils;
import tornaco.lib.media.vinci.utils.Closer;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/5/5 13:20
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class DiskCache implements Cache<String, Bitmap> {
    @Getter
    private File rootDir;

    public DiskCache(File rootDir) {
        this.rootDir = rootDir;
        try {
            Files.createParentDirs(rootDir);
        } catch (IOException e) {
            ErrorReporter.reThrow(e);
        }
    }

    @Nullable
    @Override
    public Bitmap get(@NonNull String key) {
        String targetPath = getPathFromKey(key);
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            return null;
        }
        return BitmapUtils.decodeFileLocked(targetPath, null);
    }

    @NonNull
    @Override
    public Bitmap put(@NonNull String key, @NonNull Bitmap value) {
        Enforcer.enforceWorkThread();
        Logger.d("put diskcache for key %s", key);
        String targetPath = getPathFromKey(key);
        File targetFile = new File(targetPath);
        if (targetFile.exists() && !targetFile.delete()) {
            Logger.d("Error!!! Target file exist and can not be delete");
            return value;
        }
        AtomicFile atomicFile;
        OutputStream os = null;
        try {
            Files.createParentDirs(targetFile);
            atomicFile = new AtomicFile(targetFile);
            os = atomicFile.startWrite();
            value.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (IOException ignored) {
            Logger.d("IOError!!! %s", ignored.getMessage());
        } finally {
            Closer.closeQuietly(os);
        }
        return value;
    }

    @Override
    public boolean has(@NonNull String key) {
        String targetPath = getPathFromKey(key);
        File targetFile = new File(targetPath);
        return targetFile.exists();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void evictAll() {
        Iterable<File> subFiles = Files.fileTreeTraverser().children(rootDir);
        for (File f : subFiles) {
            f.delete();
        }
    }

    private String getPathFromKey(String key) {
        return rootDir.getPath() + File.separator + key;
    }
}
