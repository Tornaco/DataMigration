package tornaco.lib.media.vinci.loader;

import android.graphics.Bitmap;

import lombok.ToString;

/**
 * Created by Nick on 2017/5/5 16:39
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@ToString
class LoadResult {

    Loader loader;
    Bitmap res;

    LoadResult() {
    }

    LoadResult(Loader loader, Bitmap res) {
        this.loader = loader;
        this.res = res;
    }
}
