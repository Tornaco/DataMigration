package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.Callable;

import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.policy.StateTracker;

/**
 * Created by Nick on 2017/5/5 13:58
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class RequestTask implements Callable<Bitmap> {

    private List<Loader> loaders;
    private String sourceUrl;
    private Consumer<LoadResult> resultConsumer;

    private StateTracker stateTracker;

    RequestTask(List<Loader> loaders, String sourceUrl,
                Consumer<LoadResult> resultConsumer, StateTracker stateTracker) {
        this.loaders = loaders;
        this.sourceUrl = sourceUrl;
        this.resultConsumer = resultConsumer;
        this.stateTracker = stateTracker;
    }

    @Override
    public Bitmap call() throws Exception {
        stateTracker.readyToGo();// Wait util we can run.

        for (Loader loader : loaders) {
            Bitmap res = loader.load(sourceUrl);
            if (res != null) {
                LoadResult result = new LoadResult(loader, res);
                resultConsumer.accept(result);
                return res;
            }
        }
        LoadResult empty = new LoadResult();
        resultConsumer.accept(empty);

        return null;
    }
}
