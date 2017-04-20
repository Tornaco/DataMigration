package org.newstand.datamigration.utils;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/20 16:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SeLinuxContextChanger {

    public static boolean restoreContext(String file) {
        if (!RootManager.getInstance().obtainPermission()) {
            Logger.d("Fail to obtain root permission");
            return false;
        }

        if (!RootTools.checkUtil("restorecon")) {
            Logger.d("Fail to check util restorecon");
            return false;
        }

        String command = "restorecon -R " + file;

        Logger.d("restoreContext with command %s", command);

        Result result = RootManager.getInstance().runCommand(command);

        Logger.d("Restore context result message %s, result %s", result.getMessage(), result.getResult());

        return result.getStatusCode() == 0;
    }
}
