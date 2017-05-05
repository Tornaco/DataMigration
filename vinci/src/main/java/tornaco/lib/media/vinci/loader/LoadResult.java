package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

/**
 * Created by Nick on 2017/5/5 16:39
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

class LoadResult {

    Loader loader;
    Bitmap res;

    public LoadResult() {
    }

    public LoadResult(Loader loader, Bitmap res) {
        this.loader = loader;
        this.res = res;
    }
}
