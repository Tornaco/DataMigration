package org.newstand.datamigration.secure;

import android.content.Context;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.newstand.datamigration.BuildConfig;
import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2;
import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VersionRetriever {

    public static String currentVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String currentBuild() {
        return BuildConfig.BUILD_TYPE;
    }

    public static void hasLaterVersionAsync(Context context,
                                            final ActionListener2<VersionCheckResult, Throwable> listener) {

        AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(context.getString(R.string.version_url)),
                new AsyncHttpClient.StringCallback() {

                    @Override
                    public void onConnect(AsyncHttpResponse response) {
                        super.onConnect(response);
                        listener.onStart();
                    }

                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                        if (e != null) {
                            listener.onError(e);
                            return;
                        }
                        final String currentVersion = BuildConfig.VERSION_NAME;
                        if (result != null) {
                            try {
                                VersionInfo versionInfo = VersionInfo.fromJson(result);
                                VersionCheckResult versionCheckResult = new VersionCheckResult();
                                versionCheckResult.setHasLater(
                                        !SettingsProvider.isTipsNoticed("checkForUpdate-" + versionInfo.getVersionName())
                                                && !currentVersion.equals(versionInfo.getVersionName()));
                                versionCheckResult.setVersionInfo(versionInfo);
                                listener.onComplete(versionCheckResult);
                            } catch (Exception e1) {
                                listener.onError(e1);
                            }
                        }
                    }
                });


    }
}
