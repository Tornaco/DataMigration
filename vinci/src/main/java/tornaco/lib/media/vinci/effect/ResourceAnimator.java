package tornaco.lib.media.vinci.effect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Nick on 2017/5/6 12:21
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class ResourceAnimator implements Animator {

    private Animation a;

    public ResourceAnimator(int id, Context context) {
        this.a = AnimationUtils.loadAnimation(context, id);
    }

    @NonNull
    @Override
    public Animation getAnimation() {
        return a;
    }
}
