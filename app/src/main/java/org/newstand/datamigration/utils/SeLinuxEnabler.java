package org.newstand.datamigration.utils;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.stericson.rootools.RootTools;

import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/18 18:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SeLinuxEnabler {


    public static SeLinuxState getSeLinuxState() {
        boolean hasEnforce = RootTools.checkUtil("getenforce");
        if (!hasEnforce) {
            Logger.e(new GetEnforceMissingError(), "Fail to get root util");
            return SeLinuxState.Unknown;
        }

        Result result = RootManager.getInstance().runCommand("getenforce");

        String message = result.getMessage();

        Logger.d("getenforce message %s", message);

        if (SeLinuxState.Enforcing.name().equals(message.trim())) {
            return SeLinuxState.Enforcing;
        }

        if (SeLinuxState.Permissive.name().equals(message.trim())) {
            return SeLinuxState.Permissive;
        }

        return SeLinuxState.Unknown;
    }

    public static boolean setState(SeLinuxState seLinuxState) {
        boolean hasEnforce = RootTools.checkUtil("setenforce");
        if (!hasEnforce) {
            Logger.e(new GetEnforceMissingError(), "Fail to get root util");
            return false;
        }
        Result result = RootManager.getInstance().runCommand("setenforce " + (seLinuxState == SeLinuxState.Permissive ? 0 : 1));

        Logger.d("setenforce res %s", result.getResult());

        return result.getResult();
    }
}
