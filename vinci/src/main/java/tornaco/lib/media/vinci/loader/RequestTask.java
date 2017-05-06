package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.Callable;

import tornaco.lib.media.vinci.common.Consumer;

/**
 * Created by Nick on 2017/5/5 13:58
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class RequestTask implements Callable<Bitmap> {

    private List<Loader> loaders;
    private String sourceUrl;
    private Consumer<LoadResult> resultConsumer;

    RequestTask(List<Loader> loaders, String sourceUrl, Consumer<LoadResult> resultConsumer) {
        this.loaders = loaders;
        this.sourceUrl = sourceUrl;
        this.resultConsumer = resultConsumer;
    }

    @Override
    public Bitmap call() throws Exception {
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
