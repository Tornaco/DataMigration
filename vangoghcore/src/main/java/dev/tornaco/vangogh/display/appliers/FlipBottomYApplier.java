package dev.tornaco.vangogh.display.appliers;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;

import dev.tornaco.vangogh.display.ImageApplier;
import dev.tornaco.vangogh.display.ImageDisplayer;
import dev.tornaco.vangogh.media.Image;

/**
 * Created by guohao4 on 2017/8/28.
 * Email: Tornaco@163.com
 */

public class FlipBottomYApplier implements ImageApplier {
    @Override
    public void apply(@NonNull final ImageDisplayer displayer, @NonNull final Image image) {
        ViewCompat.animate(displayer.getView())
                .rotationY(-90)
                .setDuration(300)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        displayer.display(image);
                        ViewCompat.animate(view)
                                .rotationY(0)
                                .setDuration(400)
                                .start();
                    }
                })
                .start();
    }

    @Override
    public long duration() {
        return 700;
    }
}
