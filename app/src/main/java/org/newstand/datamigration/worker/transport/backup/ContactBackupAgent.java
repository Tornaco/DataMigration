package org.newstand.datamigration.worker.transport.backup;

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

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.utils.Closer;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.RecordEvent;

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

public class ContactBackupAgent extends ProgressableBackupAgent<ContactBackupSettings, ContactRestoreSettings> implements
        ContextWireable, VCardEntryHandler {
    @Getter
    @Setter
    private Context context;

    @Override
    public Res backup(final ContactBackupSettings backupSettings) throws Exception {
        Writer writer = null;
        try {
            // Publish progress.
            getProgressListener().onProgress(RecordEvent.Init, 0);
            Files.createParentDirs(new File(backupSettings.getDestPath()));
            writer = new FileWriter(backupSettings.getDestPath());
            VCardComposer cardComposer = new VCardComposer(context, VCARD_TYPE_V40_GENERIC);
            ContactRecord[] records = backupSettings.getDataRecord();
            if (!cardComposer.init(buildSelections(records), buildArgs(records))) {
                return new InitFailException("Unable to init:" + cardComposer.getErrorReason());
            }
            int count = cardComposer.getCount();
            Preconditions.checkState(count >= 1, "Expected at least 1 match but got " + count);
            getProgressListener().onProgress(RecordEvent.CreateDir, 100);

            getProgressListener().onProgress(RecordEvent.Insert, 0);
            while (!cardComposer.isAfterLast()) {
                String entry = cardComposer.createOneEntry();
                writer.write(entry);
            }
            getProgressListener().onProgress(RecordEvent.Insert, 100);

            Collections.consumeRemaining(records, new Consumer<ContactRecord>() {
                @Override
                public void accept(@NonNull ContactRecord contactRecord) {
                    contactRecord.setPath(backupSettings.getDestPath());
                }
            });
        } finally {
            Closer.closeQuietly(writer);
        }
        return Res.OK;
    }

    private String buildSelections(ContactRecord[] records) {
        StringBuilder sb = new StringBuilder();
        String selection = ContactsContract.Contacts._ID + "=?";
        for (int i = 0; i < records.length; i++) {
            if (i > 0) sb.append(" or ");
            sb.append(selection);
        }
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
    public Res restore(ContactRestoreSettings restoreSettings) throws Exception {

        String sourcePath = restoreSettings.getSourcePath();
        InputStream inputStream = Files.asByteSource(new File(sourcePath)).openStream();

        getProgressListener().onProgress(RecordEvent.Insert, 0);
        VCardParser vCardParser = new VCardParser_V40(VCardSourceDetector.PARSE_TYPE_UNKNOWN);
        VCardEntryConstructor vCardEntryConstructor = new VCardEntryConstructor(VCardSourceDetector.PARSE_TYPE_UNKNOWN);
        ContentResolver resolver = getContext().getContentResolver();
        VCardEntryCommitter vCardEntryCommitter = new VCardEntryCommitter(resolver);
        vCardEntryConstructor.addEntryHandler(vCardEntryCommitter);
        vCardEntryConstructor.addEntryHandler(this);
        vCardParser.addInterpreter(vCardEntryConstructor);
        vCardParser.parse(inputStream);
        getProgressListener().onProgress(RecordEvent.Insert, 100);

        return Res.OK;
    }

    @Override
    public void wire(@NonNull Context context) {
        setContext(context);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onEntryCreated(VCardEntry entry) {
    }

    @Override
    public void onEnd() {
    }
}
