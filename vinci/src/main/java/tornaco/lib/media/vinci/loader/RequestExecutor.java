package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import tornaco.lib.media.vinci.Enforcer;
import tornaco.lib.media.vinci.ErrorReporter;
import tornaco.lib.media.vinci.Request;
import tornaco.lib.media.vinci.cache.FastSmallMemoryCache;
import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.common.Holder;
import tornaco.lib.media.vinci.display.ImageConsumer;
import tornaco.lib.media.vinci.effect.EffectProcessor;
import tornaco.lib.media.vinci.utils.BitmapUtils;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/5/5 14:01
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
public class RequestExecutor {

    private Request r;

    public static final ExecutorService CACHE_POOL_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4 + 1);

    public RequestExecutor(Request r) {
        this.r = r;
    }

    private void executeInternal(ExecutorService service) {
        final List<Loader> loaders = r.getLoaders();
        String sourceUrl = r.getSourceUrl();

        // Apply the placeholder.
        int ph = r.getPlaceHolderRes();
        if (ph > 0) {
            Bitmap holder = FastSmallMemoryCache.get().get(String.valueOf(ph));
            if (holder == null) {
                holder = BitmapUtils.getBitmap(r.getContext(), ph);
                if (holder != null) {
                    FastSmallMemoryCache.get().put(String.valueOf(ph), holder);

                    for (Consumer<Bitmap> consumer : r.getImageConsumers()) {
                        consumer.accept(holder);
                    }
                }
            }
        }

        final Holder<FutureRequestTask> requestTaskHolder = new Holder<>(null);

        FutureRequestTask fr = new FutureRequestTask(new RequestTask(loaders, sourceUrl,
                new Consumer<LoadResult>() {
                    @Override
                    public void accept(LoadResult loadResult) {

                        Logger.d("LoadResult %s", loadResult);

                        OnLoadCompleteEvent onLoadCompleteEvent =
                                new OnLoadCompleteEvent(loadResult.loader, r.getSourceUrl(), loadResult.res);
                        LoaderEventProvider.getInstance().publish(onLoadCompleteEvent);

                        RequestExecutor.this.accept(loadResult.res);

                        FutureRequestTask fft = requestTaskHolder.getHost();
                        if (fft != null) {
                            FutureTaskManager.getInstance().publish(new OnFutureTaskDoneEvent(r.getSourceUrl(), fft, r.imageConsumerId()));
                        }
                    }
                }));

        FutureTaskManager.getInstance().publish(new OnFutureTaskCommitEvent(fr, r.getSourceUrl(), r.imageConsumerId()));

        service.submit(fr);

        requestTaskHolder.setHost(fr);
    }

    public void execute() {
        executeOnExecutor(CACHE_POOL_EXECUTOR);
    }

    public void executeOnExecutor(ExecutorService service) {
        executeInternal(service);
    }

    public void accept(Bitmap bitmap) {

        if (bitmap != null) {
            EffectProcessor effectProcessor = r.getEffectProcessor();
            if (effectProcessor != null) {
                bitmap = Enforcer.enforceNonNull(effectProcessor.process(bitmap));
            }
        } else {
            // Apply from err res.
            int errRes = r.getErrorRes();
            if (errRes > 0) {
                bitmap = FastSmallMemoryCache.get().get(String.valueOf(errRes));
                if (bitmap == null) {
                    bitmap = BitmapUtils.getBitmap(r.getContext(), errRes);
                    if (bitmap != null)
                        FastSmallMemoryCache.get().put(String.valueOf(errRes), bitmap);
                }
            }
        }

        Logger.d("Before applying image size %s", r.getImageConsumers().size());

        for (ImageConsumer consumer : r.getImageConsumers()) {

            Logger.d("Now applying image to %s", consumer);
            try {
                consumer.applyAnimator(r.getAnimator());
                consumer.accept(bitmap);
            } catch (Throwable e) {
                Logger.d("Error apply %s", Logger.getStackTraceString(e));
                ErrorReporter.reThrow(e);
            }
        }
    }
}
