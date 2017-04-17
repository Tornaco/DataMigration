package org.newstand.datamigration.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

public class ViewAnimateUtils {

    public static final int DURATION_SHORT = 300;
    public static final int DURATION_MID = 800;


    public static void alphaShow(@NonNull final View view) {
        if (view.getWindowToken() == null)
            return;
        view.setVisibility(View.VISIBLE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alpha.setDuration(DURATION_SHORT);
        alpha.start();
    }

    public static void alphaHide(@NonNull final View view, final Runnable rWhenDone) {
        if (view.getWindowToken() == null) {
            if (rWhenDone != null)
                rWhenDone.run();
            return;
        }
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        alpha.setDuration(DURATION_SHORT);
        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (rWhenDone != null)
                    rWhenDone.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (rWhenDone != null)
                    rWhenDone.run();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alpha.start();
    }


    public static void scaleShow(final View view) {
        scaleShow(view, null);
    }

    public static void scaleShow(final View view, final Runnable rWhenEnd) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            if (rWhenEnd != null) {
                rWhenEnd.run();
            }
            return;
        }
        if (view.getWindowToken() == null) {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    scaleShow(view);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {

                }
            });
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(DURATION_SHORT);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
                if (rWhenEnd != null) {
                    rWhenEnd.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.VISIBLE);
                if (rWhenEnd != null) {
                    rWhenEnd.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }


    public static void scaleHide(final View view) {
        scaleHide(view, null);
    }

    public static void scaleHide(final View view, final Runnable rWhenEnd) {
        if (view.getWindowToken() == null) {
            view.setVisibility(View.INVISIBLE);
            if (rWhenEnd != null) {
                rWhenEnd.run();
            }
            return;
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(DURATION_SHORT);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (rWhenEnd != null) {
                    rWhenEnd.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                if (rWhenEnd != null) {
                    rWhenEnd.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public static void runFlipHorizonAnimation(@NonNull View view, long duration, final Runnable rWhenEnd) {
        view.setAlpha(0);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view,
                "rotationY", -180f, 0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "alpha",
                0f, 1f);
        set.setDuration(duration);
        set.playTogether(objectAnimator1, objectAnimator2);
        if (rWhenEnd != null)
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rWhenEnd.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        set.start();
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
