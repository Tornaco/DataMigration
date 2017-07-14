package org.newstand.datamigration.loader.impl;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.DataRecordComparator;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Nick@NewStand.org on 2017/3/7 10:10
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ContactLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(final LoaderFilter<DataRecord> filter) {

        final List<DataRecord> records = new ArrayList<>();

        consumeCursor(createCursor(ContactsContract.Contacts.CONTENT_URI, null, null, null, null), new Consumer<Cursor>() {
            @Override
            public void accept(@NonNull Cursor cursor) {
                DataRecord record = recordFromCursor(cursor);
                if (record != null && (filter == null || !filter.ignored(record)))
                    records.add(record);
            }
        });

        java.util.Collections.sort(records, new DataRecordComparator());

        return records;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final List<DataRecord> records = new ArrayList<>();
        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.Contact, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.Contact, session);
        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));
        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                ContactRecord record = new ContactRecord();
                record.setDisplayName(file.getName());
                record.setPath(file.getAbsolutePath());
                try {
                    record.setSize(record.calculateSize());
                } catch (IOException e) {
                    Logger.e(e, "Fail get size");
                }
                records.add(record);
            }
        });

        java.util.Collections.sort(records, new DataRecordComparator());

        return records;
    }

    private DataRecord recordFromCursor(Cursor cursor) {

        int contactIdIndex;

        int nameIndex;

        contactIdIndex = cursor
                .getColumnIndex(ContactsContract.Contacts._ID);

        nameIndex = cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        ContactRecord record = new ContactRecord();

        String contactId = cursor.getString(contactIdIndex);
        String name = cursor.getString(nameIndex);

        // Inflate name.
        record.setDisplayName(name);

        Cursor phones = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{contactId}, null);

        if (phones != null && phones.moveToNext()) {

            int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String phoneNumber = phones.getString(phoneIndex);

            // Inflate phone.
            record.setPhoneNum(phoneNumber);
        }

        if (phones != null) {
            phones.close();
        }

        Cursor email = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                new String[]{contactId}, null);

        if (email != null && email.moveToNext()) {

            int emailIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            String emailAddress = email.getString(emailIndex);

            // Inflate email.
            record.setEmail(emailAddress);
        }


        if (email != null) {
            email.close();
        }

        record.setId(contactId);

        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        record.setUri(uri);

        try {
            record.setSize(record.calculateSize());
        } catch (IOException e) {
            Logger.e(e, "Fail get size");
        }

        return record;
    }

    @Override
    public String[] needPermissions() {
        return new String[]{
                Manifest.permission.READ_CONTACTS
        };
    }
}
