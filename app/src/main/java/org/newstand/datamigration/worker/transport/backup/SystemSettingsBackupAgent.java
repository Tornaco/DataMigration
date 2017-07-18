package org.newstand.datamigration.worker.transport.backup;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.gson.Gson;

import org.newstand.datamigration.data.model.SettingsRecord;
import org.newstand.datamigration.utils.Files;
import org.newstand.logger.Logger;

/**
 * Created by Nick on 2017/6/21 16:06
 */

public class SystemSettingsBackupAgent
        extends ProgressableBackupAgent<SystemSettingsBackupSettings, SystemSettingsRestoreSettings> {

    @Override
    public Res backup(SystemSettingsBackupSettings backupSettings) throws Exception {
        SettingsRecord settingsRecord = backupSettings.getRecord();
        Gson gson = new Gson();
        String content = gson.toJson(settingsRecord);
        String dest = backupSettings.getDestPath();
        boolean writen = Files.writeString(content, dest);
        if (!writen) {
            return new WriteFailError();
        }
        settingsRecord.setPath(dest);
        return Res.OK;
    }

    @Override
    public Res restore(SystemSettingsRestoreSettings restoreSettings) throws Exception {
        if (!RootManager.getInstance().obtainPermission()) {
            return new RootMissingException();
        }
        SettingsRecord settingsRecord = restoreSettings.getRecord();
        String command = String.format("settings put %s %s %s",
                settingsRecord.getNamespace(), settingsRecord.getKey(), settingsRecord.getValue());
        Result result = RootManager.getInstance().runCommand(command);
        Logger.d("Write settings result: %s, %s, %s", result.getMessage(), result.getStatusCode(), result.getResult());
        if (!result.getResult()) {
            return new InsertFailErr();
        }
        return Res.OK;
    }
}
