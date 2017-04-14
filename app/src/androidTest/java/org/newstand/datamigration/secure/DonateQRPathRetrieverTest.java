package org.newstand.datamigration.secure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/14 14:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class DonateQRPathRetrieverTest {

    @Test
    public void getPathForDonateQRImage() throws Exception {
        String path = DonateQRPathRetriever.getPathForDonateQRImage();
        Logger.d(path);
        Assert.assertTrue(path != null);

        String cache = DonateQRPathRetriever.load(InstrumentationRegistry.getTargetContext());
        Bitmap bit = BitmapFactory.decodeFile(cache);

        Assert.assertTrue(bit != null);
    }

}