package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Nick on 2017/5/5 22:33
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class FutureRequestTask extends FutureTask<Bitmap> {

    FutureRequestTask(@NonNull Callable<Bitmap> callable) {
        super(callable);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public String toString() {
        return "FutureRequestTask" + "@" + Integer.toHexString(hashCode());
    }
}
