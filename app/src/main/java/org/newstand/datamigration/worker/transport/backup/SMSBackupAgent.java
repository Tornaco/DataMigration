package org.newstand.datamigration.worker.transport.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.Telephony;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.model.SMSRecord;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.worker.transport.RecordEvent;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import lombok.Getter;
import lombok.Setter;

import static org.newstand.datamigration.data.SmsContentProviderCompat.DRAFT_CONTENT_URI;
import static org.newstand.datamigration.data.SmsContentProviderCompat.INBOX_CONTENT_URI;
import static org.newstand.datamigration.data.SmsContentProviderCompat.SENT_CONTENT_URI;

/**
 * Created by Nick@NewStand.org on 2017/3/13 13:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
class SMSBackupAgent extends ProgressableBackupAgent<SMSBackupSettings, SMSRestoreSettings> implements ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    @Override
    public Res backup(SMSBackupSettings backupSettings) throws Exception {
        String destPath = backupSettings.getDestPath();
        Files.createParentDirs(new File(destPath));

        getProgressListener().onProgress(RecordEvent.FileCopy, 0);

        OutputStream os = Files.asByteSink(new File(destPath)).openStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(backupSettings.getSmsRecord());
        oos.flush();
        os.close();
        oos.close();

        // Encrypt
        boolean encrypt = SettingsProvider.isEncryptEnabled();
        String encrypted = SettingsProvider.getEncryptPath(destPath);
        boolean encryptOk = encrypt && EncryptManager.from(getContext())
                .encrypt(destPath, encrypted);
        if (encryptOk) {
            BlackHole.eat(new File(destPath).delete());
            Logger.i("Encrypt ok, assigning file to %s", encrypted);
            destPath = encrypted;
        }

        // Update file path
        backupSettings.getSmsRecord().setPath(destPath);

        getProgressListener().onProgress(RecordEvent.FileCopy, 100);

        return Res.OK;
    }

    @Override
    public Res restore(SMSRestoreSettings restoreSettings) throws Exception {
        // Set us as Def Sms app
        getProgressListener().onProgress(RecordEvent.WaitForSMSDefApp, 0);
        SmsContentProviderCompat.setAsDefaultSmsApp(getContext());
        getProgressListener().onProgress(RecordEvent.WaitForSMSDefApp, 50);
        boolean isDefSmsApp = SmsContentProviderCompat.waitUtilBecomeDefSmsApp(getContext());
        if (!isDefSmsApp) {
            Logger.e("Timeout waiting for DEF SMS APP setup");
            return new NotDefaultSMSAppErr();
        }
        getProgressListener().onProgress(RecordEvent.WaitForSMSDefApp, 100);
        SMSRecord smsRecord = restoreSettings.getSmsRecord();
        writeSMS(smsRecord);
        return Res.OK;
    }

    /* Write all messages contained in smsbk back to content provider */
    private void writeSMS(SMSRecord smsRecord) {

        getProgressListener().onProgress(RecordEvent.Insert, 0);

        ContentResolver cr = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        //Here we are taking only some fields
        switch (smsRecord.getMsgBox()) {
            case INBOX:
                values.put(Telephony.Sms.Inbox._ID, smsRecord.getId());
                values.put(SmsContentProviderCompat.ADDRESS, smsRecord.getAddr());
                values.put(SmsContentProviderCompat.BODY, smsRecord.getMsg());
                values.put(SmsContentProviderCompat.DATE_SENT, smsRecord.getTime());
                values.put(SmsContentProviderCompat.READ, smsRecord.getReadState());

                cr.insert(INBOX_CONTENT_URI, values);
                break;

            case SENT:
                values.put(Telephony.Sms.Sent._ID, smsRecord.getId());
                values.put(SmsContentProviderCompat.ADDRESS, smsRecord.getAddr());
                values.put(SmsContentProviderCompat.BODY, smsRecord.getMsg());
                values.put(SmsContentProviderCompat.DATE_SENT, smsRecord.getTime());
                values.put(SmsContentProviderCompat.READ, smsRecord.getReadState());

                cr.insert(SENT_CONTENT_URI, values);
                break;

            case DRAFT:
                values.put(Telephony.Sms.Draft._ID, smsRecord.getId());
                values.put(SmsContentProviderCompat.ADDRESS, smsRecord.getAddr());
                values.put(SmsContentProviderCompat.BODY, smsRecord.getMsg());
                values.put(SmsContentProviderCompat.DATE_SENT, smsRecord.getTime());
                values.put(SmsContentProviderCompat.READ, smsRecord.getReadState());

                cr.insert(DRAFT_CONTENT_URI, values);
        }

        getProgressListener().onProgress(RecordEvent.Insert, 100);
    }
}
