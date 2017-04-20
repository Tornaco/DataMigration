package org.newstand.datamigration.utils;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/4/20 15:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ZipperTest {
    @Test
    public void exec() throws Exception {
        Assert.assertTrue(Zipper.execTarCommand("tar"));

        String to = Environment.getExternalStorageDirectory().getPath() + File.separator + "Test.tar.gz";

        Assert.assertTrue(Zipper.deCompressTar(to));

    }

}