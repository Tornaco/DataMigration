package org.newstand.datamigration.worker.transport.backup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/25 16:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WifiBackupAgent implements BackupAgent<WifiBackupSettings, WifiRestoreSettings>, ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public Res backup(WifiBackupSettings backupSettings) throws Exception {
        String destPath = backupSettings.getDestPath();
        Files.createParentDirs(new File(destPath));

        Gson gson = new Gson();// FIXME Should we use single instance?
        String callStr = gson.toJson(backupSettings.getRecord());
        boolean ok = org.newstand.datamigration.utils.Files.writeString(callStr, destPath);
        if (!ok) return new WriteFailError();

        // Encrypt
        boolean encrypt = SettingsProvider.isEncryptEnabled();
        String encrypted = SettingsProvider.getEncryptPath(destPath);
        boolean encryptOk = encrypt && EncryptManager.from(getContext()).encrypt(destPath, encrypted);
        if (encryptOk) {
            BlackHole.eat(new File(destPath).delete());
            Logger.i("Encrypt ok, assigning file to %s", encrypted);
            destPath = encrypted;
        }

        // Update file path
        backupSettings.getRecord().setPath(destPath);
        return Res.OK;
    }

    @Override
    public Res restore(WifiRestoreSettings restoreSettings) throws Exception {
        final String destPath = SettingsProvider.getWifiConfigFilePath();

        List<String> rawLines = restoreSettings.getRecord().getRawLines();

        String start = String.format("echo %s >> %s", "network={", destPath);
        Result startRes = RootManager.getInstance().runCommand(start);
        Logger.v("Write start cmd %s, res %s %s", start, startRes.getMessage(), startRes.getResult());

        Collections.consumeRemaining(rawLines, new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) {
                String cmd = String.format("echo %s >> %s", "\t" + s + "\n", destPath);
                Result result = RootManager.getInstance().runCommand(cmd);
                Logger.v("Write wifi cmd %s, res %s %s", cmd, result.getMessage(), result.getResult());
            }
        });

        String end = String.format("echo %s >> %s", "}", destPath);
        Result endRes = RootManager.getInstance().runCommand(end);
        Logger.v("Write end cmd %s, res %s %s", start, endRes.getMessage(), endRes.getResult());

        return Res.OK;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }
}
