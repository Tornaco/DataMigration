package dev.tornaco.vangogh;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
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

    public static Vangogh from(Context context) {
        Assert.assertNotNull("Context is null", context);
        VangoghContext.setContext(context.getApplicationContext());
        return new Vangogh();
    }

    public static Vangogh with(Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static Vangogh with(android.support.v4.app.Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static Vangogh with(Activity activity) {
        Assert.assertNotNull("Activity is null", activity);
        VangoghContext.setContext(activity.getApplicationContext());
        Vangogh v = new Vangogh();
        v.registerLifeCycleListener(activity);
        return v;
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

    private void registerLifeCycleListener(final Activity targetActivity) {
        Application application = targetActivity.getApplication();
        application.registerActivityLifecycleCallbacks(new LifecycleCallbacksAdapter() {
            @Override
            public void onActivityDestroyed(Activity activity) {
                super.onActivityDestroyed(activity);
                if (activity == targetActivity) {
                    pause();
                    clearPendingRequests();
                    quit();
                }
            }
        });
    }

    @Getter
    public class VangoghRequest {

        private ImageSource source;
        private ImageDisplayer imageDisplayer;
        private ImageApplier applier;
        private ImageEffect[] effect;

        private LoaderObserver observer;

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
                            .requestTimeMills(System.currentTimeMillis())
                            .alias("abc")
                            .displayer(this.imageDisplayer)
                            .imageSource(this.source)
                            .applier(applier)
                            .effect(effect)
                            .id(RequestIdFactory.next())
                            .observer(observer)
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
