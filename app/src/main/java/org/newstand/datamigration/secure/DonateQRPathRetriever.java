package org.newstand.datamigration.secure;

import android.content.Context;

import com.bumptech.glide.Glide;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;

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

    public static void cacheAsync(final Context context) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String path = load(context);
                SettingsProvider.setDonateQrPath(path);
            }
        });
    }

    public static String load(Context context) {
        String path = getPathForDonateQRImage();
        try {
            File file = Glide.with(context).load(path).downloadOnly(300, 300).get();
            return file.getPath();
        } catch (Throwable e) {
            return null;
        }
    }

    public static native String getPathForDonateQRImage();
}
