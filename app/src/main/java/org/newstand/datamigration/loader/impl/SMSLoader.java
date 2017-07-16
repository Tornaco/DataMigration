package org.newstand.datamigration.loader.impl;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.MsgBox;
import org.newstand.datamigration.data.model.SMSRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.EncryptManager;
import org.newstand.datamigration.utils.BlackHole;
import org.newstand.datamigration.utils.Closer;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.newstand.datamigration.data.SmsContentProviderCompat.DRAFT_CONTENT_URI;
import static org.newstand.datamigration.data.SmsContentProviderCompat.INBOX_CONTENT_URI;
import static org.newstand.datamigration.data.SmsContentProviderCompat.SENT_CONTENT_URI;

/**
 * Created by Nick@NewStand.org on 2017/3/13 12:03
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SMSLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        Logger.d("Loading SMS");
        final List<DataRecord> res = new ArrayList<>();

        Collections.consumeRemaining(MsgBox.values(), new Consumer<MsgBox>() {
            @Override
            public void accept(@NonNull final MsgBox msgBox) {
                consumeCursor(getSmsCursor(msgBox), new Consumer<Cursor>() {
                    @Override
                    public void accept(@NonNull Cursor cursor) {
                        SMSRecord record = getSMS(cursor);
                        record.setMsgBox(msgBox);
                        res.add(record);
                    }
                });
            }
        });
        return res;
    }

    /* IMP: Works on API-level 19 (KK) */
    public Cursor getSmsCursor(MsgBox box) {
        ContentResolver cr = getContext().getContentResolver();
        Cursor c = null;

        switch (box) {
            case INBOX:
                c = cr.query(INBOX_CONTENT_URI, null, null, null, null);
                break;
            case SENT:
                c = cr.query(SENT_CONTENT_URI, null, null, null, null);
                break;
            case DRAFT:
                c = cr.query(DRAFT_CONTENT_URI, null, null, null, null);
                break;
        }

        return c;
    }

    /* Retrieves and populates SMSRecord */
    public SMSRecord getSMS(Cursor c) {
        SMSRecord sms = new SMSRecord();
        sms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
        sms.setAddr(c.getString(c.getColumnIndexOrThrow("address")));
        sms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
        sms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
        sms.setReadState(c.getString(c.getColumnIndexOrThrow("read")));
        sms.setDisplayName(sms.getMsg());
        try {
            sms.setSize(sms.calculateSize());
        } catch (IOException e) {
            Logger.e(e, "Fail get size");
        }
        return sms;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, final LoaderFilter<DataRecord> filter) {
        Logger.d("loadFromSession %s", session);
        final List<DataRecord> res = new ArrayList<>();
        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.Sms, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.Sms, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                Logger.d("Parsing file: %s", file.getPath());
                try {

                    String srcPath = file.getPath();
                    boolean isEncrypted = SettingsProvider.isEncryptedFile(srcPath);
                    String fileToDecrypt = isEncrypted ? SettingsProvider.getDecryptPath(srcPath) : null;
                    if (isEncrypted) {
                        boolean decryptOk = EncryptManager.from(getContext()).decrypt(srcPath, fileToDecrypt);
                        if (decryptOk) {
                            Logger.i("decryptOk, using decrypted %s", fileToDecrypt);
                            srcPath = fileToDecrypt;
                        }
                    }

                    InputStream in = Files.asByteSource(new File(srcPath)).openStream();
                    ObjectInputStream ois = new ObjectInputStream(in);

                    try {
                        SMSRecord smsRecord = (SMSRecord) ois.readObject();
                        if (smsRecord != null) smsRecord.setPath(srcPath);
                        if (smsRecord != null && (filter == null
                                || !filter.ignored(smsRecord))) {
                            smsRecord.setPath(srcPath);
                            smsRecord.setDisplayName(smsRecord.getMsg());
                            smsRecord.setSize(smsRecord.calculateSize());
                            res.add(smsRecord);
                        }
                    } catch (ClassNotFoundException e) {
                        Logger.e("Err when read sms:%s", e);
                    } finally {
                        // Clean up
                        if (fileToDecrypt != null) {
                            File fileToDelete = new File(fileToDecrypt);
                            if (fileToDelete.exists()) BlackHole.eat(fileToDelete.delete());
                        }
                    }

                    Closer.closeQuietly(ois);
                    Closer.closeQuietly(in);

                } catch (Throwable e) {
                    Logger.e("Err when read sms:%s", e);
                }
            }
        });
        return res;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
