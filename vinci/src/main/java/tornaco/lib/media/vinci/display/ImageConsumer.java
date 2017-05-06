package tornaco.lib.media.vinci.display;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import tornaco.lib.media.vinci.common.Consumer;
import tornaco.lib.media.vinci.effect.Animator;


/**
 * Created by Nick on 2017/5/5 12:34
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public abstract class ImageConsumer implements Consumer<Bitmap> {
    public abstract void applyAnimator(@Nullable Animator animator);

    public String identify() {
        return Integer.toHexString(hashCode());
    }
}
