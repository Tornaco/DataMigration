package org.newstand.datamigration.utils;

import android.util.Log;

import com.stericson.rootools.Constants;
import com.stericson.rootools.RootTools;
import com.stericson.rootshell.execution.Command;
import com.stericson.rootshell.execution.Shell;

import org.newstand.logger.Logger;

import java.util.Locale;

/**
 * Created by Nick@NewStand.org on 2017/4/6 13:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RootTools2 {

    public static boolean changeOwner(String fileOrDir, int owner) throws Exception {
        boolean hasChown = RootTools.checkUtil("chown");
        Logger.d("hasChown %s", hasChown);

        if (hasChown) {
            String cmdStr = String.format(Locale.ENGLISH, "chown -Rh %d %s", owner, fileOrDir);
            Logger.i("cmdStr %s", cmdStr);
            Command changeOwner = new Command(0, false, cmdStr);
            Shell.startRootShell().add(changeOwner);
            commandWait(Shell.startShell(), changeOwner);

            int exitCode = changeOwner.getExitCode();

            Logger.d("chown exit code %s", exitCode);

            return exitCode == 0;

        } else {
            return false;
        }
    }

    public static boolean changeGroup(String fileOrDir, int group) throws Exception {
        boolean hasChgrp = RootTools.checkUtil("chgrp");
        Logger.d("hasChgrp %s", hasChgrp);

        if (hasChgrp) {
            String cmdStr = String.format(Locale.ENGLISH, "chgrp -Rh %d %s", group, fileOrDir);
            Logger.i("cmdStr %s", cmdStr);
            Command changeGrp = new Command(0, false, cmdStr);
            Shell.startRootShell().add(changeGrp);

            commandWait(Shell.startShell(), changeGrp);

            int exitCode = changeGrp.getExitCode();

            Logger.d("chgrp exit code %s", exitCode);

            return exitCode == 0;

        } else {
            return false;
        }
    }

    static void commandWait(Shell shell, Command cmd) throws Exception {

        while (!cmd.isFinished()) {

            RootTools.log(Constants.TAG, shell.getCommandQueuePositionString(cmd));
            RootTools.log(Constants.TAG, "Processed " + cmd.totalOutputProcessed + " of " + cmd.totalOutput + " output delegate command.");

            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!cmd.isExecuting() && !cmd.isFinished()) {
                if (!shell.isExecuting && !shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not executing and not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                } else if (shell.isExecuting && !shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is executing but not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                } else {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not reading! \n\n Command: " + cmd.getCommand());
                    Exception e = new Exception();
                    e.setStackTrace(Thread.currentThread().getStackTrace());
                    e.printStackTrace();
                }
            }

        }
    }
}
