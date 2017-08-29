package dev.tornaco.vangogh;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import junit.framework.Assert;

import java.util.concurrent.atomic.AtomicInteger;

import dev.tornaco.vangogh.display.ImageApplier;
import dev.tornaco.vangogh.display.ImageDisplayer;
import dev.tornaco.vangogh.display.ImageEffect;
import dev.tornaco.vangogh.display.ImageViewDisplayer;
import dev.tornaco.vangogh.loader.LoaderObserver;
import dev.tornaco.vangogh.media.ImageSource;
import dev.tornaco.vangogh.request.ImageRequest;
import dev.tornaco.vangogh.request.RequestDispatcherTornaco;
import dev.tornaco.vangogh.request.RequestLooper;
import lombok.Getter;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */

public class Vangogh {

    private static final Vangogh sMe = new Vangogh();

    private RequestLooper mLooper;

    public static VangoghRequest with(Fragment fragment) {
        return with((fragment.getActivity().getApplicationContext()));
    }

    public static VangoghRequest with(Context c) {
        return with(c, VangoghConfig.defaultConfig(c));
    }

    public static VangoghRequest with(Context c, VangoghConfig config) {
        Assert.assertNotNull("Context can not be null", c);
        Assert.assertNotNull("VangoghConfig can not be null", config);
        return sMe.createRequest(c, config);
    }

    private synchronized VangoghRequest createRequest(Context c, VangoghConfig config) {
        VangoghConfigManager.getInstance().updateConfig(config);

        if (mLooper == null) {
            mLooper = RequestLooper.newInstance(new RequestDispatcherTornaco(config.getRequestPoolSize()));
        }

        VangoghRequest request = new VangoghRequest();
        request.looper = mLooper;
        request.context = c;
        return request;
    }

    public void pause() {
        mLooper.pause();
    }

    public void resume() {
        mLooper.resume();
    }

    public ImageRequest[] clearPendingRequests() {
        return mLooper.clearPendingRequests();
    }

    public void quit() {
        mLooper.quit();
    }

    @Getter
    public static class VangoghRequest {

        private RequestLooper looper;

        private Context context;

        private ImageSource source;
        private ImageDisplayer imageDisplayer;
        private ImageApplier applier;
        private ImageEffect[] effect;

        private LoaderObserver observer;

        public VangoghRequest load(String url) {
            ImageSource source = new ImageSource();
            source.setContext(context);
            source.setUrl(url);
            this.source = source;
            return this;
        }

        public VangoghRequest load(Uri uri) {
            return load(uri.toString());
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

        public VangoghRequest observer(LoaderObserver observer) {
            this.observer = observer;
            return this;
        }

        public ImageRequest into(ImageView imageView) {
            return into(new ImageViewDisplayer(imageView));
        }

        public ImageRequest into(ImageDisplayer imageDisplayer) {
            this.imageDisplayer = imageDisplayer;

            ImageRequest imageRequest =
                    ImageRequest.builder()
                            .context(context)
                            .requestTimeMills(System.currentTimeMillis())
                            .alias("abc")
                            .displayer(this.imageDisplayer)
                            .imageSource(this.source)
                            .applier(applier)
                            .effect(effect)
                            .id(RequestIdFactory.next())
                            .observer(observer)
                            .build();
            looper.onNewRequest(imageRequest);
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
