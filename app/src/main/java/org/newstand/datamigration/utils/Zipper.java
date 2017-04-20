package org.newstand.datamigration.utils;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nick@NewStand.org on 2017/4/20 15:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class Zipper {

    public static boolean execTarCommand(String command) {
        if (!RootManager.getInstance().obtainPermission()) {
            Logger.d("Fail to obtain root permission");
            return false;
        }

        if (!RootTools.checkUtil("tar")) {
            Logger.d("Fail to check util tar");
            return false;
        }

        Result result = RootManager.getInstance().runCommand(command);

        Logger.d("Result message %s, ok %s", result.getMessage(), result.getResult());

        return result.getStatusCode() == 0;
    }


    public static boolean compressTar(String to, String from) {
        String command = String.format("tar -zcvf %s %s", to, from);
        try {
            com.google.common.io.Files.createParentDirs(new File(to));
        } catch (IOException e) {
            Logger.e(e, "Fail to create parent dir for %s", to);
            return false;
        }
        Logger.d("Compressing with command %s", command);
        return execTarCommand(command);
    }

    public static boolean deCompressTar(String file) {
        String command = String.format("tar -zxvf %s", file);
        Logger.d("Decompressing with command %s", command);
        return execTarCommand(command);
    }
}
