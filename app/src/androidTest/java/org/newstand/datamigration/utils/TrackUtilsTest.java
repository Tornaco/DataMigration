package org.newstand.datamigration.utils;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/3/28 15:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class TrackUtilsTest {
    @Test
    public void extract() throws Exception {
        String artist = TrackUtils.getArtist(Environment.getExternalStorageDirectory()
                .getPath() + File.separator + "test.mp3");
        Logger.d(artist);
    }

}