package tornaco.lib.media.vinci.loader;

import android.support.annotation.NonNull;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.common.Publisher;

/**
 * Created by Nick on 2017/5/5 16:22
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class LoaderEventProvider extends Observable implements Publisher<OnLoadCompleteEvent> {

    private static final LoaderEventProvider sMe = new LoaderEventProvider();

    private static final ExecutorService DEFAULT_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);

    public static LoaderEventProvider getInstance() {
        return sMe;
    }

    @Override
    public void publish(@NonNull OnLoadCompleteEvent onLoadCompleteEvent) {
        setChanged();
        notifyObservers(onLoadCompleteEvent);
    }

    public void subscribe(final Consumer<OnLoadCompleteEvent> loaderEventConsumer) {
        subscribeOn(loaderEventConsumer, DEFAULT_SERVICE);
    }

    public void subscribeOn(final Consumer<OnLoadCompleteEvent> loaderEventConsumer,
                            final ExecutorService service) {
        addObserver(new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        OnLoadCompleteEvent e = (OnLoadCompleteEvent) arg;
                        loaderEventConsumer.accept(e);
                    }
                });
            }
        });
    }
}
