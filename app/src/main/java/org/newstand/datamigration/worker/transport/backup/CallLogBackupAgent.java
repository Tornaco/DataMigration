package org.newstand.datamigration.worker.transport.backup;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.model.CallLogRecord;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.logger.Logger;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/19 14:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CallLogBackupAgent extends ProgressableBackupAgent<CallLogBackupSettings, CallLogRestoreSettings> implements ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public Res backup(CallLogBackupSettings backupSettings) throws Exception {
        String destPath = backupSettings.getDestPath();
        Files.createParentDirs(new File(destPath));
        Gson gson = new Gson();
        String callStr = gson.toJson(backupSettings.getDataRecord()[0]);
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
        backupSettings.getDataRecord()[0].setPath(destPath);
        return Res.OK;
    }

    @Override
    public Res restore(CallLogRestoreSettings restoreSettings) throws Exception {
        CallLogRecord callLogRecord = restoreSettings.getCallLogRecord();

        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, callLogRecord.getNum());
        values.put(CallLog.Calls.DATE, callLogRecord.getDate());
        values.put(CallLog.Calls.TYPE, callLogRecord.getType());

        ContentResolver cr = getContext().getContentResolver();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            return new NoPermissionErr();
        }

        Uri uri = cr.insert(CallLog.CONTENT_URI, values);

        if (uri == null) return new InsertFailErr();

        return Res.OK;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

}
