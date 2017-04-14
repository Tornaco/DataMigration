package org.newstand.datamigration.secure;

import android.content.Context;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.newstand.datamigration.BuildConfig;
import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.common.Consumer;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VersionRetriever {

    private static void getLatestVersionStringFromServerAsync(Context context, final ActionListener2<VersionInfo, Exception> listener) {

        AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(context.getString(R.string.version_url)),
                new AsyncHttpClient.StringCallback() {

                    @Override
                    public void onConnect(AsyncHttpResponse response) {
                        super.onConnect(response);
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                        if (result != null) {
                            listener.onComplete(VersionInfo.fromJson(result));
                        } else {
                            listener.onError(e);
                        }
                    }
                });
    }

    public static void hasLaterVersionAsync(Context context,
                                            final Consumer<VersionCheckResult> versionConsumer) {
        final String currentVersion = BuildConfig.VERSION_NAME;
        getLatestVersionStringFromServerAsync(context, new ActionListener2Adapter<VersionInfo, Exception>() {
            @Override
            public void onComplete(VersionInfo versionInfo) {
                super.onComplete(versionInfo);
                VersionCheckResult result = new VersionCheckResult();
                result.setHasLater(currentVersion.equals(versionInfo.getVersionName()));
                result.setVersionInfo(versionInfo);
                versionConsumer.accept(result);
            }

            @Override
            public void onError(Exception e) {
                super.onError(e);
                VersionCheckResult result = new VersionCheckResult();
                result.setHasLater(false);
                versionConsumer.accept(result);
            }
        });
    }
}
