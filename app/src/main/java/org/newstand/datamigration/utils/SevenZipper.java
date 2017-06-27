package org.newstand.datamigration.utils;

import android.os.Process;

import com.stericson.rootools.RootTools;
import com.stericson.rootshell.execution.Command;

import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;

import static org.newstand.datamigration.utils.RootTools2.commandWait;

/**
 * Created by Nick@NewStand.org on 2017/4/20 15:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SevenZipper {

    private static boolean execTarCommand(String command) {
        Command tarCmd = new Command(0, false, command);
        try {
            RootTools.getShell(false).add(tarCmd);
            commandWait(com.stericson.rootshell.execution.Shell.startShell(), tarCmd);
        } catch (Throwable e) {
            Logger.e(e, "Fail exec command");
        }

        int exitCode = tarCmd.getExitCode();

        Logger.d("7Z exit code %s", exitCode);

        return exitCode == 0;
    }

    public static boolean compressTar(String from, String to) {
        // Make it in background p.
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

        String command = String.format("7z -y a %s %s", to, from);
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
        // Make it in background p.
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        String command = String.format("7z -y x %s", file);
        Logger.d("Decompressing with command %s", command);
        return execTarCommand(command);
    }
}
