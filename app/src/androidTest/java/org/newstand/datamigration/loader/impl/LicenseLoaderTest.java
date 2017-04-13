package org.newstand.datamigration.loader.impl;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.License;
import org.newstand.datamigration.loader.LoaderListener;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class LicenseLoaderTest {
    @Test
    public void loadAsync() throws Exception {

        License license = License.builder()
                .fullLicense("LICENSE")
                .author("Nick")
                .description("This is a test")
                .url("https://github.com/Tornaco?tab=stars")
                .version("0.0.1")
                .build();

        Gson gson = new Gson();
        String gStr = gson.toJson(license);

        Logger.d(gStr);

        LicenseLoader.loadAsync(InstrumentationRegistry.getTargetContext(), new LoaderListener<License>() {
            @Override
            public void onStart() {
                Logger.d("onStart");
            }

            @Override
            public void onComplete(Collection<License> collection) {
                Logger.d("onComplete");

                Collections.consumeRemaining(collection, new Consumer<License>() {
                    @Override
                    public void accept(@NonNull License license) {
                        Logger.w("Got---%s", license);
                    }
                });
            }

            @Override
            public void onErr(Throwable throwable) {
                Logger.d("onErr");
            }
        });

        Sleeper.sleepQuietly(3 * 1000);
    }

}