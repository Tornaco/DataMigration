package org.newstand.datamigration.secure;

/**
 * Created by Nick@NewStand.org on 2017/4/14 14:38
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DonateQRPathRetriever {

    static {
        System.loadLibrary("aio-lib");
    }

    public static native String getPathForDonateQRImage();
}
