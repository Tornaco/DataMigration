package tornaco.lib.media.vinci.effect;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Nick on 2017/5/5 11:22
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public interface EffectProcessor {
    @NonNull
    Bitmap process(@NonNull Bitmap source);
}
