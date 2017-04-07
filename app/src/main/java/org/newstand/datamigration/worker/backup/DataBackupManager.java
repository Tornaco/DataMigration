package org.newstand.datamigration.worker.backup;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.AbortException;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.common.ContextWireable;
import org.newstand.datamigration.common.StartSignal;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.FileBasedRecord;
import org.newstand.datamigration.data.model.SMSRecord;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.Stats;
import org.newstand.datamigration.worker.backup.session.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/3/8 17:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBackupManager {
    @Getter
    private Context context;

    @Getter
    private Session session;

    public static DataBackupManager from(Context context) {
        return new DataBackupManager(context, Session.create());
    }

    public static DataBackupManager from(Context context, Session session) {
        return new DataBackupManager(context, session);
    }

    public boolean renameSessionChecked(Session session, String name) {
        if (!name.equals(session.getName())) {
            String dir = SettingsProvider.getBackupRootDir();
            File from = new File(dir + File.separator + session.getName());
            File to = new File(dir + File.separator + name);
            try {
                Files.move(from, to);
                session.rename(name);
                return true;
            } catch (IOException e) {
                Logger.e("Fail to rename session %s", e.getLocalizedMessage());
            }
        }
        return false;
    }

    public void performBackup(final Collection<DataRecord> dataRecords,
                              final DataCategory dataCategory) {
        new BackupWorker(new BackupRestoreListenerAdapter(), dataRecords, dataCategory, new AbortSignal()).run();
    }

    public AbortSignal performBackupAsync(final Collection<DataRecord> dataRecords,
                                          final DataCategory dataCategory,
                                          final BackupRestoreListener listener) {

        return performBackupAsync(dataRecords, dataCategory, listener, null);
    }

    public AbortSignal performBackupAsync(final Collection<DataRecord> dataRecords,
                                          final DataCategory dataCategory,
                                          final BackupRestoreListener listener,
                                          final StartSignal startSignal) {

        AbortSignal abortSignal = new AbortSignal();
        final BackupWorker worker = new BackupWorker(listener, dataRecords, dataCategory, abortSignal);
        listener.setStats(worker.status);
        if (startSignal == null) {
            SharedExecutor.execute(worker);
        } else {
            startSignal.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    SharedExecutor.execute(worker);
                }
            });
        }
        return abortSignal;
    }

    public AbortSignal performRestoreAsync(final Collection<DataRecord> dataRecords,
                                           final DataCategory dataCategory,
                                           final BackupRestoreListener listener) {
        return performRestoreAsync(dataRecords, dataCategory, listener, null);
    }

    public AbortSignal performRestoreAsync(final Collection<DataRecord> dataRecords,
                                           final DataCategory dataCategory,
                                           final BackupRestoreListener listener,
                                           final StartSignal startSignal) {
        AbortSignal abortSignal = new AbortSignal();
        final RestoreWorker worker = new RestoreWorker(listener, dataRecords, dataCategory, abortSignal);
        listener.setStats(worker.status);
        if (startSignal == null) {
            SharedExecutor.execute(worker);
        } else {
            startSignal.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    SharedExecutor.execute(worker);
                }
            });
        }
        return abortSignal;
    }

    private BackupSettings getBackupSettingsByCategory(DataCategory dataCategory, DataRecord record) {
        switch (dataCategory) {
            case Music:
            case Photo:
            case Video:
            case CustomFile:
                return getFileBackupSettings(dataCategory, record);
            case App:
                AppBackupSettings appBackupSettings = new AppBackupSettings();
                appBackupSettings.setAppRecord((AppRecord) record);
                appBackupSettings.setDestApkPath(
                        SettingsProvider.getBackupDirByCategory(dataCategory, session)
                                + File.separator + record.getDisplayName()
                                + File.separator + SettingsProvider.getBackupAppApkDirName()
                                + File.separator + record.getDisplayName() + AppRecord.APK_FILE_PREFIX);// DMBK2/APP/Phone/apk/XX.apk
                appBackupSettings.setDestDataPath(
                        SettingsProvider.getBackupDirByCategory(dataCategory, session)
                                + File.separator + record.getDisplayName()
                                + File.separator + SettingsProvider.getBackupAppDataDirName());// DMBK2/APP/Phone/data
                appBackupSettings.setSourceApkPath(((FileBasedRecord) record).getPath());
                appBackupSettings.setSourceDataPath(SettingsProvider.getAppDataDir() + File.separator + ((AppRecord) record).getPkgName());
                return appBackupSettings;
            case Contact:
                ContactBackupSettings contactBackupSettings = new ContactBackupSettings();
                contactBackupSettings.setDataRecord(new ContactRecord[]{(ContactRecord) record});
                contactBackupSettings.setDestPath(SettingsProvider.getBackupDirByCategory(dataCategory, session)
                        + File.separator + record.getDisplayName() + "@" + record.getId() + ContactBackupSettings.SUBFIX);
                return contactBackupSettings;
            case Sms:
                SMSBackupSettings smsBackupSettings = new SMSBackupSettings();
                smsBackupSettings.setSmsRecord((SMSRecord) record);
                smsBackupSettings.setDestPath(SettingsProvider.getBackupDirByCategory(dataCategory, session)
                        + File.separator + record.getId());
                return smsBackupSettings;
        }
        throw new IllegalArgumentException("Unknown for:" + dataCategory.name());
    }

    private FileBackupSettings getFileBackupSettings(DataCategory category, DataRecord record) {
        FileBackupSettings fileBackupSettings = new FileBackupSettings();
        FileBasedRecord fileBasedRecord = (FileBasedRecord) record;
        fileBackupSettings.setSourcePath(fileBasedRecord.getPath());
        fileBackupSettings.setDestPath(SettingsProvider.getBackupDirByCategory(category, session)
                + File.separator + record.getDisplayName());
        return fileBackupSettings;
    }

    private RestoreSettings getRestoreSettingsByCategory(DataCategory dataCategory, DataRecord record) {
        switch (dataCategory) {
            case Music:
            case Photo:
            case Video:
            case CustomFile:
                FileRestoreSettings fileRestoreSettings = new FileRestoreSettings();
                fileRestoreSettings.setSourcePath(((FileBasedRecord) record).getPath());
                fileRestoreSettings.setDestPath(SettingsProvider.getRestoreDirByCategory(dataCategory, session)
                        + File.separator + record.getDisplayName());
                return fileRestoreSettings;
            case App:
                AppRestoreSettings appRestoreSettings = new AppRestoreSettings();
                appRestoreSettings.setSourceApkPath(((FileBasedRecord) record).getPath());
                appRestoreSettings.setSourceDataPath(SettingsProvider.getBackupDirByCategory(dataCategory, session)
                        + File.separator + record.getDisplayName()
                        + File.separator + SettingsProvider.getBackupAppDataDirName()
                        + File.separator + "*");// DMBK2/APP/Phone/data/*
                appRestoreSettings.setDestDataPath(SettingsProvider.getAppDataDir() + File.separator + ((AppRecord) record).getPkgName());
                appRestoreSettings.setAppRecord((AppRecord) record);
                return appRestoreSettings;
            case Contact:
                ContactRestoreSettings contactRestoreSettings = new ContactRestoreSettings();
                ContactRecord contactRecord = (ContactRecord) record;
                contactRestoreSettings.setSourcePath(contactRecord.getPath());
                return contactRestoreSettings;
            case Sms:
                SMSRestoreSettings smsRestoreSettings = new SMSRestoreSettings();
                smsRestoreSettings.setSourcePath(((SMSRecord) record).getPath());
                return smsRestoreSettings;

        }
        throw new IllegalArgumentException("Unknown for:" + dataCategory.name());
    }

    private BackupAgent getAgentByCategory(DataCategory category) {
        switch (category) {
            case Music:
                return new MusicBackupAgent();
            case Photo:
                return new PhotoBackupAgent();
            case Video:
                return new VideoBackupAgent();
            case CustomFile:
                return new FileBackupAgent();
            case Contact:
                return new ContactBackupAgent();
            case App:
                return new AppBackupAgent();
            case Sms:
                return new SMSBackupAgent();
        }
        throw new IllegalArgumentException("Unknown for:" + category.name());
    }

    private class BackupWorker implements Runnable {

        BackupRestoreListener listener;
        Collection<DataRecord> dataRecords;
        DataCategory dataCategory;

        SimpleStats status;

        boolean canceled;

        public BackupWorker(BackupRestoreListener listener,
                            Collection<DataRecord> dataRecords,
                            final DataCategory dataCategory,
                            AbortSignal abortSignal) {
            this.listener = listener;
            this.dataRecords = dataRecords;
            this.dataCategory = dataCategory;
            this.status = new SimpleStats();
            this.status.init(dataRecords.size());
            abortSignal.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    canceled = true;
                    Logger.w("BackupWorker canceled %s", dataCategory.name());
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            final BackupAgent backupAgent = getAgentByCategory(dataCategory);
            if (backupAgent instanceof ContextWireable) {
                ContextWireable contextWireable = (ContextWireable) backupAgent;
                contextWireable.wire(getContext());
            }
            listener.onStart();
            Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
                @Override
                public void consume(@NonNull DataRecord record) {
                    if (canceled) {
                        listener.onPieceFail(record, new AbortException());
                        status.onFail();
                        return;
                    }
                    listener.onPieceStart(record);
                    BackupSettings settings = getBackupSettingsByCategory(dataCategory, record);
                    try {
                        BackupAgent.Res res = backupAgent.backup(settings);
                        if (BackupAgent.Res.isOk(res)) {
                            listener.onPieceSuccess(record);
                            status.onSuccess();
                        } else {
                            listener.onPieceFail(record, res);
                            status.onFail();
                        }
                    } catch (Exception e) {
                        listener.onPieceFail(record, e);
                        status.onFail();
                    }
                }
            });

            // Write the session info.
            String infoFilePath = SettingsProvider.getBackupSessionInfoPath(session);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(session);
            try {
                Files.asCharSink(new File(infoFilePath), Charset.defaultCharset()).write(jsonStr);
                Logger.v("Session info has been written to %s", infoFilePath);
            } catch (IOException e) {
                Logger.e("Fail to write session info, WTF??? %s", Logger.getStackTraceString(e));
            }

            listener.onComplete();
        }
    }

    private class RestoreWorker implements Runnable {

        BackupRestoreListener listener;
        Collection<DataRecord> dataRecords;
        DataCategory dataCategory;

        SimpleStats status;

        boolean canceled;

        public RestoreWorker(BackupRestoreListener listener,
                             Collection<DataRecord> dataRecords,
                             final DataCategory dataCategory,
                             AbortSignal signal) {
            this.listener = listener;
            this.dataRecords = dataRecords;
            this.dataCategory = dataCategory;
            this.status = new SimpleStats();
            this.status.init(dataRecords.size());

            signal.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    canceled = true;
                    Logger.w("RestoreWorker canceled %s", dataCategory.name());
                }
            });
        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            final BackupAgent backupAgent = getAgentByCategory(dataCategory);
            if (backupAgent instanceof ContextWireable) {
                ContextWireable contextWireable = (ContextWireable) backupAgent;
                contextWireable.wire(getContext());
            }
            listener.onStart();
            Collections.consumeRemaining(dataRecords, new Consumer<DataRecord>() {
                @Override
                public void consume(@NonNull DataRecord record) {
                    if (canceled) {
                        listener.onPieceFail(record, new AbortException());
                        status.onFail();
                        return;
                    }
                    listener.onPieceStart(record);
                    RestoreSettings settings = getRestoreSettingsByCategory(dataCategory, record);
                    try {
                        BackupAgent.Res res = backupAgent.restore(settings);
                        if (BackupAgent.Res.isOk(res)) {
                            listener.onPieceSuccess(record);
                            status.onSuccess();
                        } else {
                            listener.onPieceFail(record, res);
                            status.onFail();
                        }
                    } catch (Exception e) {
                        listener.onPieceFail(record, e);
                        status.onFail();
                    }
                }
            });
            listener.onComplete();
        }
    }

    @ToString
    private class SimpleStats implements Stats {

        @Setter(AccessLevel.PACKAGE)
        @Getter
        private int total, left, success, fail;

        private void init(int size) {
            total = left = size;
            Logger.d("init status %s", toString());
        }

        private void onPiece() {
            left--;
        }

        @Override
        public void onSuccess() {
            success++;
            onPiece();
        }

        @Override
        public void onFail() {
            fail++;
            onPiece();
        }

        @Override
        public Stats merge(Stats with) {

            total += with.getTotal();
            left += with.getLeft();
            success += with.getSuccess();
            fail += with.getFail();

            return this;
        }
    }
}
