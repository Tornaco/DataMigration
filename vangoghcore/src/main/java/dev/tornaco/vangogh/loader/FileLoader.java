package dev.tornaco.vangogh.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

import dev.tornaco.vangogh.common.Error;
import dev.tornaco.vangogh.media.BitmapImage;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */

public class FileLoader extends BaseImageLoader {

    @Override
    boolean canHandleType(@Nullable ImageSource.SourceType type) {
        return type == ImageSource.SourceType.File;
    }

    @Nullable
    @Override
    Image doLoad(@NonNull ImageSource source, @Nullable LoaderObserver observer) {
        Logger.v("FileLoader, doLoad: %s", source);
        if (observer != null) observer.onImageLoading(source);

        String filePath = getSplitPath(source, ImageSource.SourceType.File);
        Logger.v("FileLoader, getSplitPath: %s", filePath);

        if (!new File(filePath).exists()) {
            Error error = Error.fileNotFound(filePath);
            if (observer != null) {
                observer.onImageFailure(error);
            }
            return null;
        }

        if (!new File(filePath).canRead()) {
            Error error = Error.fileNotReadable(filePath);
            if (observer != null) {
                observer.onImageFailure(error);
            }
            return null;
        }

        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtil.decodeFile(source.getContext(), filePath);
        } catch (IOException e) {
            Error error = Error.io(e);
            if (observer != null) {
                observer.onImageFailure(error);
            }
            return null;
        }

        Logger.i("decodeFile bitmap: %s", bitmap);

        Image image = new BitmapImage(bitmap, "file");
        if (observer != null) {
            observer.onImageReady(image);
        }
        return image;
    }

    @Override
    public int priority() {
        return 1;
    }
}
