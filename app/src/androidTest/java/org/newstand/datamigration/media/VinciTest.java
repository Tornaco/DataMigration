package org.newstand.datamigration.media;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.R;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;

import tornaco.lib.media.vinci.Vinci;
import tornaco.lib.media.vinci.display.ImageConsumer;
import tornaco.lib.media.vinci.effect.Animator;
import tornaco.lib.media.vinci.effect.EffectProcessor;
import tornaco.lib.media.vinci.loader.Loader;
import tornaco.lib.media.vinci.loader.Priority;

/**
 * Created by Nick on 2017/5/5 12:24
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class VinciTest {

    @Test
    public void getInstanceTest() {

        Vinci.load(InstrumentationRegistry.getTargetContext(),
                "drawable://ic_help")
                .processor(new EffectProcessor() {
                    @NonNull
                    @Override
                    public Bitmap process(@NonNull Bitmap source) {
                        Logger.d("VinciTest-process %s", source);
                        return source;
                    }
                })
                .loader(new Loader() {
                    @Nullable
                    @Override
                    public Bitmap load(@NonNull String sourceUrl) {
                        Logger.d("VinciTest-load %s", sourceUrl);
                        Sleeper.sleepQuietly(Interval.Minutes.getIntervalMills());
                        return null;
                    }

                    @Override
                    public int priority() {
                        return Priority.A;
                    }
                })
                .error(R.drawable.github)
                .placeHolder(R.drawable.forktocat)
                .into(new ImageConsumer() {
                    @Override
                    public void applyAnimator(@Nullable Animator animator) {

                    }

                    @Override
                    public void accept(Bitmap bitmap) {

                    }
                });

        boolean canceled = Vinci.cancel("drawable://ic_help");

        Assert.assertTrue(canceled);

        Sleeper.sleepQuietly(Interval.Minutes.getIntervalMills());
    }

    @Test
    public void pauseTest() {
        for (int i = 0; i < 20; i++) {
            Vinci.load(InstrumentationRegistry.getTargetContext(), "drawable://ic_help")
                    .into(new ImageConsumer() {
                        @Override
                        public void applyAnimator(@Nullable Animator animator) {

                        }

                        @Override
                        public void accept(Bitmap bitmap) {

                        }
                    });
        }

        Vinci.pause();

        Sleeper.sleepQuietly(Interval.Seconds.getIntervalMills() * 10);

        try {
            Vinci.pause();
        } catch (Throwable expected) {
            Logger.d(expected);
        }

        Vinci.cancelAllRequests();

        Vinci.resume();
    }
}
