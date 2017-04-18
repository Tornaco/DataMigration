package org.newstand.datamigration.secure;

import android.content.Context;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.logger.Logger;

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
        AsyncHttpClient.getDefaultInstance().executeFile(new AsyncHttpGet(path), toPath, new AsyncHttpClient.FileCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, File result) {
                if (result == null) {
                    Logger.e(e, "Loading QR fail");
                    return;
                }
                Logger.d("Loading QR onCompleted@ %s", result.getPath());
                SettingsProvider.setDonateQrPath(result.getPath());
            }
        });
    }

    public static native String getPathForDonateQRImage();
}
