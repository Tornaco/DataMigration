package org.newstand.datamigration.utils;

import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nick on 2017/6/27 17:53
 */

public class NoMediaUtil {

    public static void createNoMediaFileAsync(final String dir) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                createNoMediaFile(dir);
            }
        });
    }

    public static void createNoMediaFile(String dir) {
        String filePath = dir + File.separator + ".nomedia";
        File file = new File(filePath);
        if (file.exists()) {
            return;
        }
        Logger.d("Create nomedia file to:%s", filePath);
        try {
            com.google.common.io.Files.createParentDirs(file);
        } catch (IOException e) {
            Logger.e(e, "Fail create dir");
        }
        try {
            com.google.common.io.Files.touch(file);
        } catch (IOException e) {
            Logger.e(e, "Fail touch file");
        }
    }
}
