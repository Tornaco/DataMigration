package org.newstand.datamigration.worker.backup.mms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import org.newstand.datamigration.data.model.MsgBox;
import org.newstand.datamigration.data.model.SMSBackup;
import org.newstand.datamigration.data.model.SMSRecord;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import lombok.Getter;

/*
   SMS Backup : currently not dealing with settings
   During backup, we use SMSBackup object to keep a copy all messages.
   We serialize and write it to "smsBackup" file. Reverse operation
   during restore. Currently, only a few fields are used to backup.
 */

public class QTIBackupSMS {

    Cursor cursor;
    String vfile;
    Context mContext;
    private final boolean DEBUG = true;
    private final String TAG = "DataMigration-QTISMS";
    @Getter
    SMSBackup smsBackupInbox, smsBackupSent, smsBackupDraft;
    FileOutputStream mFileOutputStream;
    FileInputStream mFileInputStream;
    BufferedOutputStream buf;

    public QTIBackupSMS(Context context, String _vfile) {
        mContext = context;
        vfile = _vfile;
        smsBackupInbox = new SMSBackup();
        smsBackupSent = new SMSBackup();
        smsBackupDraft = new SMSBackup();
    }

    public void performRestore() {
        SMSBackup smsbk;
        try {
            mFileInputStream = mContext.openFileInput(vfile);
            ObjectInputStream ois = new ObjectInputStream(mFileInputStream);

            //FOR Inbox
            int numOfObjects = ois.readInt();
            if (numOfObjects < 1) {
                if (DEBUG) Log.d(TAG, "No messages to restore in Inbox");
            }
            if (DEBUG) Log.d(TAG, "numOfObjects: " + numOfObjects);
            smsbk = (SMSBackup) ois.readObject();
            writeSMS(smsbk, MsgBox.INBOX);

            //For Sent
            numOfObjects = ois.readInt();
            if (numOfObjects < 1) {
                if (DEBUG) Log.d(TAG, "No messages to restore in Sent");
            }
            if (DEBUG) Log.d(TAG, "numOfObjects: " + numOfObjects);
            smsbk = (SMSBackup) ois.readObject();
            writeSMS(smsbk, MsgBox.SENT);

            //For Draft
            numOfObjects = ois.readInt();
            if (numOfObjects < 1) {
                if (DEBUG) Log.d(TAG, "No messages to restore in Draft");
            }
            if (DEBUG) Log.d(TAG, "numOfObjects: " + numOfObjects);
            smsbk = (SMSBackup) ois.readObject();
            writeSMS(smsbk, MsgBox.DRAFT);

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (StreamCorruptedException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    /* Write all messages contained in smsbk back to content provider */
    private void writeSMS(SMSBackup smsbk, MsgBox box) {

        ContentResolver cr = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        //Here we are taking only some fields
        switch (box) {
            case INBOX:
                for (SMSRecord smsObj : smsbk.SMSList) {

                    values.put(Telephony.Sms.Inbox._ID, smsObj.getId());
                    values.put(Telephony.Sms.Inbox.ADDRESS, smsObj.getAddr());
                    values.put(Telephony.Sms.Inbox.BODY, smsObj.getMsg());
                    values.put(Telephony.Sms.Inbox.DATE_SENT, smsObj.getTime());
                    values.put(Telephony.Sms.Inbox.READ, smsObj.getReadState());

                    cr.insert(INBOX_CONTENT_URI, values);
                }
                break;

            case SENT:
                for (SMSRecord smsObj : smsbk.SMSList) {

                    values.put(Telephony.Sms.Sent._ID, smsObj.getId());
                    values.put(Telephony.Sms.Sent.ADDRESS, smsObj.getAddr());
                    values.put(Telephony.Sms.Sent.BODY, smsObj.getMsg());
                    values.put(Telephony.Sms.Sent.DATE_SENT, smsObj.getTime());
                    values.put(Telephony.Sms.Sent.READ, smsObj.getReadState());

                    cr.insert(SENT_CONTENT_URI, values);
                }
                break;

            case DRAFT:
                for (SMSRecord smsObj : smsbk.SMSList) {

                    values.put(Telephony.Sms.Draft._ID, smsObj.getId());
                    values.put(Telephony.Sms.Draft.ADDRESS, smsObj.getAddr());
                    values.put(Telephony.Sms.Draft.BODY, smsObj.getMsg());
                    values.put(Telephony.Sms.Draft.DATE_SENT, smsObj.getTime());
                    values.put(Telephony.Sms.Draft.READ, smsObj.getReadState());

                    cr.insert(DRAFT_CONTENT_URI, values);
                }
        }
    }

    public void performBackup() {
        int numOfEntriesInbox = getSMSList(MsgBox.INBOX);
        int numOfEntriesSent = getSMSList(MsgBox.SENT);
        int numOfEntriesDraft = getSMSList(MsgBox.DRAFT);
        writeSMSList(numOfEntriesInbox, numOfEntriesSent, numOfEntriesDraft);
    }

    /* Returns number of messages retrieved */
    public int getSMSList(MsgBox box) {
        SMSRecord smsRecord;
        cursor = getSmsCursor(box);
        if (cursor == null) {
            return -1;
        }

        int numOfEntries = cursor.getCount();
        if (numOfEntries < 1) {
            if (DEBUG) Log.d(TAG, "No messages in Your Phone");
            return 0;
        }

        cursor.moveToFirst();
        numOfEntries = 0;
        switch (box) {
            case INBOX:
                for (int i = 0; ; i++) {
                    smsRecord = getSMS(cursor);
                    smsBackupInbox.SMSList.add(smsRecord);
                    numOfEntries++;

                    if (DEBUG) Log.d(TAG, "SMS " + (i + 1) + smsBackupInbox.SMSList.get(i));
                    if (cursor.isLast()) {
                        break;
                    } else {
                        cursor.moveToNext();
                    }
                }
                break;

            case SENT:
                for (int i = 0; ; i++) {
                    smsRecord = getSMS(cursor);
                    smsBackupSent.SMSList.add(smsRecord);
                    numOfEntries++;

                    if (DEBUG) Log.d(TAG, "SMS " + (i + 1) + smsBackupSent.SMSList.get(i));
                    if (cursor.isLast()) {
                        break;
                    } else {
                        cursor.moveToNext();
                    }
                }
                break;

            case DRAFT:
                for (int i = 0; ; i++) {
                    smsRecord = getSMS(cursor);
                    smsBackupDraft.SMSList.add(smsRecord);
                    numOfEntries++;

                    if (DEBUG) Log.d(TAG, "SMS " + (i + 1) + smsBackupDraft.SMSList.get(i));
                    if (cursor.isLast()) {
                        break;
                    } else {
                        cursor.moveToNext();
                    }
                }
                break;
        }

        cursor.close();
        return numOfEntries;
    }

    /* writes SMSBackup object to file. Note that we write number of objects at start.*/
    public void writeSMSList(int numOfEntriesInbox, int numOfEntriesSent, int numOfEntriesDraft) {
        try {
            mFileOutputStream = mContext.openFileOutput(vfile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(mFileOutputStream);
            oos.writeInt(numOfEntriesInbox);
            oos.writeObject(smsBackupInbox);

            oos.writeInt(numOfEntriesSent);
            oos.writeObject(smsBackupSent);

            oos.writeInt(numOfEntriesDraft);
            oos.writeObject(smsBackupDraft);

            oos.flush();
            oos.close();
            mFileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    /* Retrieves and populates SMSRecord */
    public SMSRecord getSMS(Cursor c) {
        SMSRecord sms = new SMSRecord();

        sms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
        sms.setAddr(c.getString(c.getColumnIndexOrThrow("address")));
        sms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
        sms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
        sms.setReadState(c.getString(c.getColumnIndexOrThrow("read")));

        return sms;
    }

    /* IMP: Works on API-level 19 (KK) */
    public Cursor getSmsCursor(MsgBox box) {
        ContentResolver cr = mContext.getContentResolver();
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

    public static final Uri INBOX_CONTENT_URI = Uri.parse("content://sms/inbox");
    public static final Uri SENT_CONTENT_URI = Uri.parse("content://sms/sent");
    public static final Uri DRAFT_CONTENT_URI = Uri.parse("content://sms/draft");
}
