package org.newstand.datamigration.worker.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.android.vcard.VCardComposer;
import com.android.vcard.VCardEntry;
import com.android.vcard.VCardEntryCommitter;
import com.android.vcard.VCardEntryConstructor;
import com.android.vcard.VCardEntryHandler;
import com.android.vcard.VCardParser;
import com.android.vcard.VCardParser_V40;
import com.android.vcard.VCardSourceDetector;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.utils.Closer;
import org.newstand.datamigration.utils.Collections;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;

import lombok.Getter;
import lombok.Setter;

import static com.android.vcard.VCardConfig.VCARD_TYPE_V40_GENERIC;

/**
 * Created by Nick@NewStand.org on 2017/3/9 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ContactBackupAgent implements BackupAgent<ContactBackupSettings, ContactRestoreSettings>, ContextWireable, VCardEntryHandler {
    @Getter
    @Setter
    private Context context;

    @Override
    public void backup(final ContactBackupSettings backupSettings) throws Exception {
        Logger.d("backup with settings:%s", backupSettings);
        Writer writer = null;
        try {
            Files.createParentDirs(new File(backupSettings.getDestPath()));
            writer = new FileWriter(backupSettings.getDestPath());
            VCardComposer cardComposer = new VCardComposer(context, VCARD_TYPE_V40_GENERIC);
            ContactRecord[] records = backupSettings.getDataRecord();
            if (!cardComposer.init(buildSelections(records), buildArgs(records))) {
                throw new IllegalStateException("Unable to init:" + cardComposer.getErrorReason());
            }
            int count = cardComposer.getCount();
            Logger.e("Found:" + count);
            Preconditions.checkState(count >= 1, "Expected at least 1 match but got " + count);

            while (!cardComposer.isAfterLast()) {
                String entry = cardComposer.createOneEntry();
                Logger.w(entry);
                writer.write(entry);
            }

            Collections.consumeRemaining(records, new Consumer<ContactRecord>() {
                @Override
                public void consume(@NonNull ContactRecord contactRecord) {
                    contactRecord.setPath(backupSettings.getDestPath());
                }
            });
        } finally {
            Closer.closeQuietly(writer);
        }
    }

    private String buildSelections(ContactRecord[] records) {
        StringBuilder sb = new StringBuilder();
        String selection = ContactsContract.Contacts._ID + "=?";
        for (int i = 0; i < records.length; i++) {
            if (i > 0) sb.append(" or ");
            sb.append(selection);
        }
        Logger.d(sb.toString());
        return sb.toString();
    }

    private String[] buildArgs(ContactRecord[] records) {
        String[] args = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            args[i] = records[i].getId();
        }
        return args;
    }

    @Override
    public void restore(ContactRestoreSettings restoreSettings) throws Exception {

        String sourcePath = restoreSettings.getSourcePath();
        InputStream inputStream = Files.asByteSource(new File(sourcePath)).openStream();

        VCardParser vCardParser = new VCardParser_V40(VCardSourceDetector.PARSE_TYPE_UNKNOWN);
        VCardEntryConstructor vCardEntryConstructor = new VCardEntryConstructor(VCardSourceDetector.PARSE_TYPE_UNKNOWN);
        ContentResolver resolver = getContext().getContentResolver();
        VCardEntryCommitter vCardEntryCommitter = new VCardEntryCommitter(resolver);
        vCardEntryConstructor.addEntryHandler(vCardEntryCommitter);
        vCardEntryConstructor.addEntryHandler(this);
        vCardParser.addInterpreter(vCardEntryConstructor);
        vCardParser.parse(inputStream);
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    @Override
    public void onStart() {
        Logger.d("VCardEntryHandler:onStart");
    }

    @Override
    public void onEntryCreated(VCardEntry entry) {
        Logger.d("VCardEntryHandler:onEntryCreated:%s", entry);
    }

    @Override
    public void onEnd() {
        Logger.d("VCardEntryHandler:onEnd");
    }
}
