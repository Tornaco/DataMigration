package dev.tornaco.vangogh.request;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;

import junit.framework.Assert;

import org.newstand.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import dev.tornaco.vangogh.common.Error;
import dev.tornaco.vangogh.display.ImageEffect;
import dev.tornaco.vangogh.loader.LoaderObserver;
import dev.tornaco.vangogh.loader.LoaderObserverAdapter;
import dev.tornaco.vangogh.loader.LoaderProxy;
import dev.tornaco.vangogh.media.DrawableImage;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;
import lombok.Synchronized;

/**
 * Created by guohao4 on 2017/8/24.
 * Email: Tornaco@163.com
 */
public class RequestDispatcherTornaco implements RequestDispatcher {

    private LoaderProxy proxy;
    private ExecutorService executorService;

    private final DisplayRequestDispatcher displayRequestDispatcher;

    private final Map<ImageRequest, RequestFuture> REQUESTS = new HashMap<>();

    public RequestDispatcherTornaco(int poolSize) {
        this.proxy = new LoaderProxy();
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.displayRequestDispatcher = new DisplayRequestDispatcherTornaco();
    }

    @Override
    @Synchronized
    public void dispatch(@NonNull final ImageRequest imageRequest) {
        Logger.v("RequestDispatcherTornaco, dispatch: %s", imageRequest);
        Assert.assertNotNull("ImageRequest is null", imageRequest);
        Assert.assertNotNull("Image source is null", imageRequest.getImageSource());

        // Apply placeholder.
        final ImageSource source = imageRequest.getImageSource();
        if (source.getPlaceHolder() >= 0) {
            Drawable placeHolderDrawable =
                    source.getPlaceHolder() > 0 ?
                            imageRequest.getContext().getResources()
                                    .getDrawable(source.getPlaceHolder())
                            : null;
            displayRequestDispatcher.dispatch(new DisplayRequest(
                    new DrawableImage(placeHolderDrawable),
                    imageRequest, "no-applier"));
        }

        cancel(imageRequest, true);

        final LoaderObserver observer = imageRequest.getObserver();

        this.executorService.execute(new RequestFuture(imageRequest,
                new LoaderObserverAdapter() {
                    @Override
                    public void onImageFailure(@NonNull Error error) {
                        super.onImageFailure(error);
                        if (observer != null) observer.onImageFailure(error);
                        Logger.v("RequestDispatcherTornaco.LoaderObserverAdapter, onImageFailure: %s",
                                Logger.getStackTraceString(error.getThrowable()));
                    }

                    @Override
                    public void onImageLoading(@NonNull ImageSource source) {
                        super.onImageLoading(source);
                        if (observer != null) observer.onImageLoading(source);
                        Logger.v("RequestDispatcherTornaco.LoaderObserverAdapter, onImageLoading: %s", source);
                    }

                    @Override
                    public void onImageReady(@NonNull Image image) {
                        if (observer != null) observer.onImageReady(image);
                        Logger.v("RequestDispatcherTornaco.LoaderObserverAdapter, onImageReadyToDisplay: %s", image);

                        RequestDispatcherTornaco.this.onImageReady(imageRequest, image);
                    }
                }));
    }

    @Override
    public boolean cancel(@NonNull ImageRequest imageRequest, boolean interruptRunning) {
        imageRequest.setDirty(true);

        final RequestFuture future = REQUESTS.remove(imageRequest);
        Logger.i("RequestDispatcherTornaco, cancel future: %s", future);

        if (future == null) return false;

        // FIXME. Too ugly.
        // Hook ID.
        DisplayRequest proxyRequest = new DisplayRequest(null, ImageRequest.builder().id(future.id).build(), null);
        displayRequestDispatcher.cancel(proxyRequest, interruptRunning);

        return future.cancel(interruptRunning);
    }

    @Override
    public void cancelAll(boolean interruptRunning) {
        synchronized (REQUESTS) {
            for (RequestFuture rf : REQUESTS.values()) {
                rf.cancel(interruptRunning);
            }
            REQUESTS.clear();
        }
    }

    @Override
    public void quit() {
        executorService.shutdownNow();
        synchronized (REQUESTS) {
            REQUESTS.clear();
        }
        displayRequestDispatcher.quit();
    }

    private void onImageReady(ImageRequest request, @NonNull Image image) {
        DisplayRequest displayRequest = new DisplayRequest(image, request, null);
        ImageEffect[] effects = displayRequest.getImageSource().getEffect();
        Image effectedImage = displayRequest.getImage();
        if (effects != null) {
            for (ImageEffect e : effects) {
                effectedImage = e.process(displayRequest.getContext(), effectedImage);
            }
            displayRequest.setImage(effectedImage);
        }
        displayRequestDispatcher.dispatch(displayRequest);
        // Publish used image.
        ImageManager.getInstance().onImageReadyToDisplay(displayRequest.getImageSource(), effectedImage);
    }

    private class RequestFuture extends FutureTask<Image> {

        private int id;

        RequestFuture(final ImageRequest imageRequest, final LoaderObserver observer) {

            super(new Callable<Image>() {
                @Override
                public Image call() throws Exception {

                    Image image = proxy.load(imageRequest, observer);

                    synchronized (REQUESTS) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //noinspection Since15
                            REQUESTS.remove(imageRequest, this);
                        } else {
                            REQUESTS.remove(imageRequest);
                        }
                    }
                    return image;
                }
            });

            this.id = imageRequest.getId();

            synchronized (REQUESTS) {
                REQUESTS.put(imageRequest, this);
            }
        }
    }
}
