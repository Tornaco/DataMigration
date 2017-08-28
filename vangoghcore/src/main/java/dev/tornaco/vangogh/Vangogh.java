package dev.tornaco.vangogh;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import junit.framework.Assert;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import dev.tornaco.vangogh.display.ImageApplier;
import dev.tornaco.vangogh.display.ImageDisplayer;
import dev.tornaco.vangogh.display.ImageEffect;
import dev.tornaco.vangogh.display.ImageRequest;
import dev.tornaco.vangogh.display.ImageViewDisplayer;
import dev.tornaco.vangogh.media.ImageSource;
import dev.tornaco.vangogh.request.RequestDispatcherTornaco;
import dev.tornaco.vangogh.request.RequestLooper;
import lombok.Getter;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */

public class Vangogh {

    private static Vangogh sMe;

    private RequestLooper mLooper;

    private Vangogh() {
        mLooper = RequestLooper.newInstance(new RequestDispatcherTornaco());
    }

    public static void setDiskCacheDir(File diskCacheDir) {
        VangoghContext.setDiskCacheDir(diskCacheDir);
    }

    public static void setRequestPoolSize(int requestPoolSize) {
        VangoghContext.setRequestPoolSize(requestPoolSize);
    }

    public synchronized static Vangogh from(Context context) {
        Assert.assertNotNull("Context is null", context);
        VangoghContext.setContext(context);
        if (sMe == null) sMe = new Vangogh();
        return sMe;
    }

    public VangoghRequest load(String url) {
        ImageSource source = new ImageSource();
        source.setUrl(url);
        VangoghRequest vangoghRequest = new VangoghRequest();
        vangoghRequest.source(source);
        return vangoghRequest;
    }

    public VangoghRequest load(Uri uri) {
        return load(uri.toString());
    }

    @Getter
    public class VangoghRequest {

        private ImageSource source;
        private ImageDisplayer imageDisplayer;
        private ImageApplier applier;
        private ImageEffect[] effect;

        private VangoghRequest source(ImageSource source) {
            this.source = source;
            return this;
        }

        public VangoghRequest placeHolder(@DrawableRes int drawableRes) {
            source.setPlaceHolder(drawableRes);
            return this;
        }

        public VangoghRequest fallback(@DrawableRes int fallbackRes) {
            source.setFallback(fallbackRes);
            return this;
        }

        public VangoghRequest applier(ImageApplier animator) {
            this.applier = animator;
            return this;
        }

        public VangoghRequest effect(ImageEffect... effect) {
            this.effect = effect;
            return this;
        }

        public VangoghRequest skipMemoryCache(boolean skip) {
            source.setSkipMemoryCache(skip);
            return this;
        }

        public VangoghRequest skipDiskCache(boolean skip) {
            source.setSkipDiskCache(skip);
            return this;
        }

        public ImageRequest into(ImageView imageView) {
            return into(new ImageViewDisplayer(imageView));
        }

        public ImageRequest into(ImageDisplayer imageDisplayer) {
            this.imageDisplayer = imageDisplayer;

            ImageRequest imageRequest =
                    ImageRequest.builder()
                            .requestTimeMills(System.currentTimeMillis())
                            .alias("abc")
                            .displayer(this.imageDisplayer)
                            .imageSource(this.source)
                            .applier(applier)
                            .effect(effect)
                            .id(RequestIdFactory.next())
                            .build();
            mLooper.onNewRequest(imageRequest);
            return imageRequest;
        }

    }

    private static class RequestIdFactory {
        private static final AtomicInteger S_ID = new AtomicInteger(0);

        public static int next() {
            return S_ID.getAndIncrement();
        }
    }
}
