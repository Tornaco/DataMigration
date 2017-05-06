package tornaco.lib.media.vinci;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import lombok.Getter;
import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.display.ImageConsumer;
import tornaco.lib.media.vinci.loader.FutureTaskManager;
import tornaco.lib.media.vinci.loader.OnFutureTaskCommitEvent;
import tornaco.lib.media.vinci.loader.OnFutureTaskDoneEvent;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/2/10 11:15
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class Vinci implements Executor {

    private static Vinci sOnlyOne;

    @Getter
    private Map<String, FutureTask<Bitmap>> sourceUrlTaskMap;
    @Getter
    private Map<String, FutureTask<Bitmap>> consumerTaskMap;

    private Handler mWorkThreadHandler;

    /**
     * @return Single instance of {@link Vinci}.
     */
    public synchronized static Vinci config(VinciConfig config) {
        Enforcer.enforce(sOnlyOne == null, "Duplicate config is not allowed.");
        sOnlyOne = new Vinci(config);
        return sOnlyOne;
    }

    private static Vinci enforcedGet() {
        Enforcer.enforce(sOnlyOne != null, "Please config Vinci first!");
        return sOnlyOne;
    }

    private Vinci(VinciConfig config) {
        RequestFactory.init(config);
        Logger.d("Init da vinci with %s", config);

        HandlerThread handlerThread = new HandlerThread("Vinci-handler-thread");
        handlerThread.start();
        mWorkThreadHandler = new Handler(handlerThread.getLooper());

        sourceUrlTaskMap = Maps.newHashMap();
        consumerTaskMap = Maps.newHashMap();

        FutureTaskManager.getInstance().subscribeCommitEvent(
                new Consumer<OnFutureTaskCommitEvent>() {
                    @Override
                    public void accept(OnFutureTaskCommitEvent onFutureTaskCommitEvent) {

                        // Cancel by consumer.
                        cancelByConsumerId(onFutureTaskCommitEvent.getImageConsumerId());

                        String url = onFutureTaskCommitEvent.getSourceUrl();
                        String id = onFutureTaskCommitEvent.getImageConsumerId();
                        FutureTask<Bitmap> f = onFutureTaskCommitEvent.getTask();

                        sourceUrlTaskMap.put(url, f);
                        consumerTaskMap.put(id, f);
                        Logger.d("Vinci: put to tasks %s", onFutureTaskCommitEvent);
                    }
                });

        FutureTaskManager.getInstance().subscribeDoneEvent(new Consumer<OnFutureTaskDoneEvent>() {
            @Override
            public void accept(OnFutureTaskDoneEvent onFutureTaskDoneEvent) {
                Logger.d("Vinci: remove from tasks %s", onFutureTaskDoneEvent);

                String url = onFutureTaskDoneEvent.getSourceUrl();
                String id = onFutureTaskDoneEvent.getImageConsumerId();

                Object o = sourceUrlTaskMap.remove(url);

                o = null;

                o = consumerTaskMap.remove(id);

                o = null; // Make it clear.
            }
        });
    }

    /**
     * @param context Application context is preferred.
     * @return {@link Request} instance.
     */
    public
    @NonNull
    static Request load(Context context,
                        @NonNull String sourceUri) {
        return RequestFactory
                .newRequest(context.getApplicationContext(),
                        Enforcer.enforceNonNull(sourceUri))
                .earlyExecutor(enforcedGet());
    }

    public static boolean cancel(@NonNull String sourceUrl) {
        return cancelBuSourceUrl(Enforcer.enforceNonNull(sourceUrl));
    }

    public static boolean cancel(@NonNull ImageConsumer consumer) {
        return cancelByConsumerId(Enforcer.enforceNonNull(consumer).identify());
    }

    private static boolean cancelByConsumerId(@NonNull String consumerId) {
        FutureTask<Bitmap> f = enforcedGet().getConsumerTaskMap().get(consumerId);
        Logger.d("cancelByConsumerId %s %s", consumerId, f);
        if (f != null) {
            if (f.isDone() || f.isCancelled()) {
                enforcedGet().getConsumerTaskMap().remove(consumerId);
                return true;
            } else {
                f.cancel(true);
                enforcedGet().getConsumerTaskMap().remove(consumerId);
                return true;
            }
        }
        return false;
    }

    private static boolean cancelBuSourceUrl(@NonNull String sourceUrl) {
        FutureTask<Bitmap> f = enforcedGet().getSourceUrlTaskMap().get(sourceUrl);
        Logger.d("cancel %s %s", sourceUrl, f);
        if (f != null) {
            if (f.isDone() || f.isCancelled()) {
                enforcedGet().getSourceUrlTaskMap().remove(sourceUrl);
                return true;
            } else {
                f.cancel(true);
                enforcedGet().getSourceUrlTaskMap().remove(sourceUrl);
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mWorkThreadHandler.post(command);
    }
}
