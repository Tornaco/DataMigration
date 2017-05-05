package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import tornaco.lib.media.vinci.Enforcer;
import tornaco.lib.media.vinci.Request;
import tornaco.lib.media.vinci.common.Consumer;
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
public class RequestExecutor implements Consumer<LoadResult> {

    private Request r;

    public static final ExecutorService CACHE_POOL_EXECUTOR = Executors.newCachedThreadPool();

    public RequestExecutor(Request r) {
        this.r = r;
        Logger.dbg("Request %s", r);
    }

    private void executeInternal(ExecutorService service) {
        List<Loader> loaders = r.getLoaders();
        String sourceUrl = r.getSourceUrl();

        // Before submit, apply the placeholder.
        int ph = r.getPlaceHolderRes();
        if (ph > 0) {
            Bitmap holder = BitmapUtils.getBitmap(r.getContext(), ph);
            for (Consumer<Bitmap> consumer : r.getImageConsumers()) {
                consumer.accept(holder);
            }
        }

        service.submit(new LoadRunner(loaders, sourceUrl, this));
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
            if (errRes > 0)
                bitmap = BitmapUtils.getBitmap(r.getContext(), errRes);
        }

        for (ImageConsumer consumer : r.getImageConsumers()) {
            consumer.applyAnimator(r.getAnimator());
            consumer.accept(bitmap);
        }
    }

    @Override
    public void accept(LoadResult loadResult) {

        OnLoadCompleteEvent onLoadCompleteEvent = new OnLoadCompleteEvent(loadResult.loader, r.getSourceUrl(), loadResult.res);
        LoaderEventProvider.getInstance().publish(onLoadCompleteEvent);

        accept(loadResult.res);
    }
}
