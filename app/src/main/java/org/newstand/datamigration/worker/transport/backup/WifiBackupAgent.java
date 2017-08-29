package org.newstand.datamigration.worker.transport.backup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.WifiUtils;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/25 16:43
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

class WifiBackupAgent extends ProgressableBackupAgent<WifiBackupSettings, WifiRestoreSettings>
        implements ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public Res backup(WifiBackupSettings backupSettings) throws Exception {
        String destPath = backupSettings.getDestPath();
        Files.createParentDirs(new File(destPath));

        getProgressListener().onProgress(RecordEvent.FileCopy, 0);
        Gson gson = new Gson();
        String str = gson.toJson(backupSettings.getRecord());
        getProgressListener().onProgress(RecordEvent.FileCopy, 50);
        boolean ok = org.newstand.datamigration.utils.Files.writeString(str, destPath);
        if (!ok) return new WriteFailError();
        getProgressListener().onProgress(RecordEvent.FileCopy, 100);

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
        Logger.d("Updating path to: %s", destPath);
        backupSettings.getRecord().setPath(destPath);
        return Res.OK;
    }

    @Override
    public Res restore(WifiRestoreSettings restoreSettings) throws Exception {

        // Disable wifi first.
        if (!WifiUtils.setWifiEnabled(getContext(), false)) {
            return new ToogleStateErr();
        }

        String destPath = SettingsProvider.getWifiConfigFilePath();

        String node = restoreSettings.getRecord().toString();

        File tmpDir = Files.createTempDir();

        String randomFilePath = tmpDir.getPath() + File.separator + UUID.randomUUID().toString();

        if (!org.newstand.datamigration.utils.Files.writeString(node, randomFilePath)) {
            return new WriteFailError();
        }

        getProgressListener().onProgress(RecordEvent.Insert, 0);
        String cmd = String.format("cat %s >> %s", randomFilePath, destPath);

        Result startRes = RootManager.getInstance().runCommand(cmd);
        Logger.v("Write start cmd:%s,\n msg: %s, res: %s", cmd, startRes.getMessage(), startRes.getResult());

        BlackHole.eat(new File(randomFilePath));
        getProgressListener().onProgress(RecordEvent.Insert, 100);

        return Res.OK;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }
}
