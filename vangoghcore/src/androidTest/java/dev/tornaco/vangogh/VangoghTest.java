package dev.tornaco.vangogh;

import android.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import org.junit.Test;
import org.newstand.logger.Logger;

import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.display.ImageDisplayer;

/**
 * Created by guohao4 on 2017/8/25.
 * Email: Tornaco@163.com
 */
public class VangoghTest {
    @Test
    public void from() throws Exception {
        Vangogh vangogh = Vangogh.from(InstrumentationRegistry.getTargetContext());
        Assert.assertNotNull(vangogh);
        vangogh.load("file://abc")
                .into(new ImageDisplayer() {
                    @Override
                    public void display(@android.support.annotation.NonNull @NonNull Image image) {
                        Logger.d("display: %s", image);
                    }
                });
    }

}