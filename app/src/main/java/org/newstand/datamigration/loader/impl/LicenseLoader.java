package org.newstand.datamigration.loader.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.License;
import org.newstand.datamigration.loader.LoaderListener;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import static org.newstand.datamigration.provider.SettingsProvider.licenseRootDir;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:17
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class LicenseLoader {

    public static void loadAsync(final Context context, final LoaderListener<License> loaderListener) {


        Runnable r = new Runnable() {
            @Override
            public void run() {

                loaderListener.onStart();

                final Collection<License> res = new ArrayList<>();

                final AssetManager assetManager = context.getAssets();
                try {

                    final String root = licenseRootDir();
                    String[] subDirs = assetManager.list(root);

                    Collections.consumeRemaining(subDirs, new Consumer<String>() {
                        @Override
                        public void consume(@NonNull String s) {

                            String currentDirPath = root + File.separator + s;
                            String infoPath = currentDirPath + File.separator + "license.info";

                            try {
                                BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(infoPath)));
                                StringBuilder content = new StringBuilder();
                                String line;
                                while ((line = br.readLine()) != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        content.append(System.lineSeparator()).append(line);
                                    } else {
                                        content.append("\n").append(line);
                                    }
                                }
                                br.close();

                                String liStr = content.toString();

                                Gson gson = new Gson();
                                License license = gson.fromJson(liStr, License.class);

                                license.setAssetsDir(currentDirPath);

                                res.add(license);
                            } catch (IOException e) {
                                Logger.e("Fail to open file in assets %s", e.getLocalizedMessage());
                                loaderListener.onErr(e);
                            }
                        }
                    });

                    loaderListener.onComplete(res);

                } catch (IOException e) {
                    Logger.e("Fail to list in assets %s", e.getLocalizedMessage());
                    loaderListener.onErr(e);
                }
            }
        };

        SharedExecutor.execute(r);
    }
}
