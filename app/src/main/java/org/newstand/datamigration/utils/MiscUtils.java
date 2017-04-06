package org.newstand.datamigration.utils;

import android.content.Context;
import android.content.Intent;

import com.chrisplus.rootmanager.container.Result;

import org.newstand.logger.Logger;

import java.io.File;

/**
 * Created by Nick@NewStand.org on 2017/4/6 9:46
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class MiscUtils {

    public static void printResult(Result result) {
        printResult("Misc", result);
    }

    public static void printResult(String tag, Result result) {
        Logger.i(tag + "-Result %s, %s, %s", result.getResult(), result.getMessage(), result.getStatusCode());
    }

    public static void installApkByIntent(Context context, String appFile) {
        Logger.d("Installing pkg %s", appFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Files.getUriForFile(context, new File(appFile)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
