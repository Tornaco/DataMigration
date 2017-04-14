package org.newstand.datamigration.secure;

import android.content.Context;

import com.google.common.base.Optional;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;

/**
 * Created by Nick@NewStand.org on 2017/4/14 15:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VersionRetriever {

    public static void getLatestVersionStringFromServerAsync(Context context, final Consumer<String> versionConsumer) {
        AsyncHttpClient.getDefaultInstance().executeString(new AsyncHttpGet(context.getString(R.string.version_url)),
                new AsyncHttpClient.StringCallback() {
                    @Override
                    public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                        Optional<String> res = Optional.fromNullable(result);
                        versionConsumer.accept(res.or("Unknown"));
                    }
                });
    }
}
