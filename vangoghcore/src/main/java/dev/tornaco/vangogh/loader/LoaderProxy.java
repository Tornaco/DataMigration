package dev.tornaco.vangogh.loader;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.tornaco.vangogh.VangoghContext;
import dev.tornaco.vangogh.common.Error;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;
import dev.tornaco.vangogh.display.ImageRequest;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */

public class LoaderProxy {

    private final List<Loader<Image>> LOADERS = new ArrayList<>();

    private LoaderProxy() {

        LOADERS.add(new CacheLoader());
        LOADERS.add(new FileLoader());
        LOADERS.add(new ContentLoader());


        // Read loader init Manifest.
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = VangoghContext.getContext().getPackageManager()
                    .getApplicationInfo(VangoghContext.getContext().getPackageName(),
                            PackageManager.GET_META_DATA);

            if (applicationInfo != null && applicationInfo.metaData != null) {
                String loaderClass = applicationInfo.metaData.getString("vangogh.image.loader.CLASSNAME");
                Logger.v("LoaderProxy, loaderClass: %s", loaderClass);
                if (loaderClass != null) {
                    try {
                        Class loaderClz = Class.forName(loaderClass);
                        Object obj = loaderClz.newInstance();
                        //noinspection unchecked
                        LOADERS.add((Loader<Image>) obj);
                    } catch (Throwable e) {
                        Logger.e(e, "LoaderProxy, Fail create loader instance.");
                    }
                }
            } else {
                Logger.v("LoaderProxy, no metadata.");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(e, "WTF, can not find ApplicationInfo for myself?");
        }


        Collections.sort(LOADERS, new Comparator<Loader<Image>>() {
            @Override
            public int compare(Loader<Image> l1, Loader<Image> l2) {
                if (l1.priority() > l2.priority()) return 1;
                return -1;
            }
        });
    }

    public static LoaderProxy newInstance() {
        return new LoaderProxy();
    }

    @Nullable
    public Image load(@NonNull ImageRequest imageRequest, @Nullable LoaderObserver observer) {

        Logger.v("LoaderProxy, load: %s", imageRequest);

        LoaderObserverDelegate delegate = new LoaderObserverDelegate(observer);

        for (Loader<Image> imageLoader : LOADERS) {
            Image image = imageLoader.load(imageRequest.getImageSource(), delegate);
            if (image != null && (image.asBitmap() != null || image.asDrawable() != null))
                return image;
        }

        // We got null image, or invalid image.
        delegate.onImageFailure(Error.builder()
                .errorCode(Error.ERR_CODE_GENERIC).throwable(new Throwable()).build());

        return null;
    }

    private class LoaderObserverDelegate implements LoaderObserver {

        @Nullable
        private LoaderObserver observer;

        LoaderObserverDelegate(@Nullable LoaderObserver observer) {
            this.observer = observer;
        }

        public void onImageLoading(@NonNull ImageSource source) {
            if (observer != null) {
                observer.onImageLoading(source);
            }
        }

        public void onImageReady(@NonNull Image image) {
            if (observer != null) {
                observer.onImageReady(image);
            }
        }

        public void onImageFailure(@NonNull Error error) {
            if (observer != null) {
                observer.onImageFailure(error);
            }
        }
    }
}
