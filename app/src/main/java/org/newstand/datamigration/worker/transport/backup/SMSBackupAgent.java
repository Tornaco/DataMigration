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
import org.newstand.datamigration.utils.Closer;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
class SMSBackupAgent implements BackupAgent<SMSBackupSettings, SMSRestoreSettings>, ContextWireable {

    @Getter
    @Setter
    private Context context;

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    @Override
    public Res backup(SMSBackupSettings backupSettings) throws Exception {
        Logger.d("backup with settings:%s", backupSettings);
        String destPath = backupSettings.getDestPath();
        Files.createParentDirs(new File(destPath));

        OutputStream os = Files.asByteSink(new File(destPath)).openStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(backupSettings.getSmsRecord());
        oos.flush();
        os.close();
        oos.close();

        // Update file path
        backupSettings.getSmsRecord().setPath(destPath);

        return Res.OK;
    }

    @Override
    public Res restore(SMSRestoreSettings restoreSettings) throws Exception {

        Logger.d("restore with settings:%s", restoreSettings);
        String srcPath = restoreSettings.getSourcePath();
        File file = new File(srcPath);
        try {
            InputStream in = Files.asByteSource(file).openStream();
            ObjectInputStream ois = new ObjectInputStream(in);

            try {
                SMSRecord smsRecord = (SMSRecord) ois.readObject();
                Logger.d("Found %s", smsRecord);
                writeSMS(smsRecord);
            } catch (ClassNotFoundException e) {
                Logger.e(e, "Err when read sms");
            }

            Closer.closeQuietly(ois);
            Closer.closeQuietly(in);

        } catch (IOException e) {
            Logger.e(e, "Err when read sms");
        }

        return Res.OK;
    }

    /* Write all messages contained in smsbk back to content provider */
    private void writeSMS(SMSRecord smsRecord) {

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
    }
}
