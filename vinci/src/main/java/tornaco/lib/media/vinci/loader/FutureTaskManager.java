package tornaco.lib.media.vinci.loader;

import android.support.annotation.NonNull;

import java.util.Observable;
import java.util.Observer;

import tornaco.lib.media.vinci.common.Consumer;

/**
 * Created by Nick on 2017/5/5 22:05
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class FutureTaskManager extends Observable {

    private static final FutureTaskManager sManager = new FutureTaskManager();

    public static FutureTaskManager getInstance() {
        return sManager;
    }

    public void subscribeCommitEvent(final Consumer<OnFutureTaskCommitEvent> commitEventConsumer) {
        addObserver(new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                Arg a = (Arg) arg;
                if (EventType.Commit == a.type) {
                    commitEventConsumer.accept((OnFutureTaskCommitEvent) a.event);
                }
            }
        });
    }

    public void subscribeDoneEvent(final Consumer<OnFutureTaskDoneEvent> doneEventConsumer) {
        addObserver(new Observer() {
            @Override
            public void update(Observable o, final Object arg) {
                Arg a = (Arg) arg;
                if (EventType.Done == a.type) {
                    doneEventConsumer.accept((OnFutureTaskDoneEvent) a.event);
                }
            }
        });
    }

    public void publish(@NonNull OnFutureTaskCommitEvent onFutureTaskCommitEvent) {
        setChanged();
        Arg a = new Arg();
        a.event = onFutureTaskCommitEvent;
        a.type = EventType.Commit;
        notifyObservers(a);
    }

    public void publish(@NonNull OnFutureTaskDoneEvent onFutureTaskDoneEvent) {
        setChanged();
        Arg a = new Arg();
        a.event = onFutureTaskDoneEvent;
        a.type = EventType.Done;
        notifyObservers(a);
    }

    private static class Arg {
        Object event;
        EventType type;
    }

    private enum EventType {
        Commit, Done
    }
}
