package dev.tornaco.vangogh.display.appliers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import org.newstand.logger.Logger;

import dev.tornaco.vangogh.display.ImageApplier;
import dev.tornaco.vangogh.display.ImageDisplayer;
import dev.tornaco.vangogh.media.Image;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */

public class FadeInApplier implements ImageApplier {

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    public void apply(@NonNull final ImageDisplayer displayer, @NonNull final Image image) {
        Logger.v("FadeInApplier, apply to: %s", displayer);
        ObjectAnimator animator = ObjectAnimator.ofFloat(displayer, "alpha", 1f, 0f);
        animator.setDuration(150);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                displayer.display(image);
                ObjectAnimator.ofFloat(displayer, "alpha", 0f, 1f).start();
            }
        });
        animator.start();
    }

    @Override
    public long duration() {
        return 500;
    }
}
