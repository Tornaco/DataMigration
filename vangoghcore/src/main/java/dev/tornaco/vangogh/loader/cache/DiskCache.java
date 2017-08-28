package dev.tornaco.vangogh.loader.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.newstand.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import dev.tornaco.vangogh.VangoghContext;
import dev.tornaco.vangogh.loader.BitmapUtil;
import dev.tornaco.vangogh.media.BitmapImage;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

public class DiskCache implements Cache<ImageSource, Image> {

    private static DiskCache sMe;

    private File cacheDir;

    private final Object lock = new Object();

    private DiskCache(CachePolicy cachePolicy) {
        this.cacheDir = cachePolicy.getDiskCacheDir();
    }

    static synchronized DiskCache init(CachePolicy cachePolicy) {
        if (sMe == null) sMe = new DiskCache(cachePolicy);
        return sMe;
    }

    private String createFileNameFromSource(ImageSource source) {
        return String.valueOf(source.hashCode());
    }

    @Nullable
    @Override
    public Image get(@NonNull ImageSource source) {
        String fileName = createFileNameFromSource(source);
        String filePath = cacheDir.getPath() + File.separator + fileName;
        File cacheFile = new File(filePath);
        if (!cacheFile.exists()) return null;
        try {
            return new BitmapImage(BitmapUtil.decodeFile(VangoghContext.getContext(), filePath));
        } catch (IOException e) {
            Logger.e(e, "Error when decode file");
        }
        return null;
    }

    @Override
    public boolean put(@NonNull ImageSource source, @NonNull Image image) {
        return putLocked(source, image);
    }

    private boolean putLocked(@NonNull ImageSource source, @NonNull Image image) {
        Assert.assertNotNull("Bitmap is null", image.asBitmap());
        String fileName = createFileNameFromSource(source);
        String filePath = cacheDir.getPath() + File.separator + fileName;
        File cacheFile = new File(filePath);
        if (!cacheFile.getParentFile().exists() && !cacheFile.getParentFile().mkdirs()) {
            Logger.e(new IOException("Fail create parent dir"), filePath);
            return false;
        }
        synchronized (this.lock) {
            try {
                FileOutputStream fos = new FileOutputStream(filePath);
                //noinspection ConstantConditions
                image.asBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Logger.e(e, "Fail create file output stream");
                return false;
            } catch (IOException e) {
                Logger.e(e, "Fail create file output stream");
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {

    }
}
