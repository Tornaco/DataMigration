package org.newstand.datamigration.secure;

import android.content.Context;

import org.newstand.datamigration.provider.SettingsProvider;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/4/14 14:38
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DonateQRPathRetriever {

    static {
        System.loadLibrary("aio-lib");
    }

    public static void loadAndCache(Context context) {
        String path = getPathForDonateQRImage();
        String toPath = SettingsProvider.getCommonDataDir() + File.separator + "QR";
    }

    public static native String getPathForDonateQRImage();
}
