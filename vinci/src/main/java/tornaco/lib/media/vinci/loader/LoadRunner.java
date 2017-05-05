package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.utils.Logger;

/**
 * Created by Nick on 2017/5/5 13:58
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class LoadRunner extends FutureTask<Bitmap> {

    LoadRunner(final List<Loader> loaders, final String sourceUrl,
               final Consumer<LoadResult> resultConsumer) {
        super(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                for (Loader loader : loaders) {
                    Bitmap res = loader.load(sourceUrl);
                    Logger.dbg("Calling loader %s, %s", loader, res);
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
        });
    }

}
