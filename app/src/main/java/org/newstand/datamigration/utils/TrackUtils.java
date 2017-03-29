package org.newstand.datamigration.utils;

/**
 * Created by Nick@NewStand.org on 2017/3/28 15:09
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TrackUtils {

    static {
        System.loadLibrary("audio-lib");
    }

    public static native String getArtist(String filePath);

    public static native void extractArt(String filePath, String toPath);
}
