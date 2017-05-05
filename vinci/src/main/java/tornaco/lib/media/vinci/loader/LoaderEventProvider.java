package tornaco.lib.media.vinci.loader;

import android.support.annotation.NonNull;

import java.util.Observable;
import java.util.Observer;

import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.common.Publisher;

/**
 * Created by Nick on 2017/5/5 16:22
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class LoaderEventProvider extends Observable implements Publisher<OnLoadCompleteEvent> {

    private static final LoaderEventProvider sMe = new LoaderEventProvider();

    public static LoaderEventProvider getInstance() {
        return sMe;
    }

    @Override
    public void publish(@NonNull OnLoadCompleteEvent onLoadCompleteEvent) {
        setChanged();
        notifyObservers(onLoadCompleteEvent);
    }

    public void subscribe(final Consumer<OnLoadCompleteEvent> loaderEventConsumer) {
        addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                OnLoadCompleteEvent e = (OnLoadCompleteEvent) arg;
                loaderEventConsumer.accept(e);
            }
        });
    }
}
