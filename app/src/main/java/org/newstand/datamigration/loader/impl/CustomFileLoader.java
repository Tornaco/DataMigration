package org.newstand.datamigration.loader.impl;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.common.io.Files;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.CustomFileRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderFilter;
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
 * Created by Nick@NewStand.org on 2017/3/30 17:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CustomFileLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        return loadFrom(Environment.getExternalStorageDirectory().getPath(), new LoaderFilter<DataRecord>() {
            @Override
            public boolean ignored(@NonNull DataRecord ths) {
                CustomFileRecord fb = (CustomFileRecord) ths;
                if (fb.isDir()) return true;
                String parentName = Files.getNameWithoutExtension(new File(fb.getPath()).getParent());
                return Environment.DIRECTORY_MOVIES.equals(parentName)
                        || Environment.DIRECTORY_MUSIC.equals(parentName)
                        || Environment.DIRECTORY_PICTURES.equals(parentName)
                        || Environment.DIRECTORY_NOTIFICATIONS.equals(parentName)
                        || Environment.DIRECTORY_DOWNLOADS.equals(parentName)
                        || Environment.DIRECTORY_RINGTONES.equals(parentName)
                        || Environment.DIRECTORY_PODCASTS.equals(parentName)
                        || Environment.DIRECTORY_DCIM.equals(parentName)
                        || Environment.DIRECTORY_ALARMS.equals(parentName)
                        || "Config".equals(parentName)
                        || "Android".equals(parentName)
                        || "Documents".equals(parentName)
                        || parentName.startsWith(".");
            }
        });
    }

    @Override
    public Collection<DataRecord> loadFromSession(Session session, LoaderFilter<DataRecord> filter) {
        return loadFrom(SettingsProvider.getBackupDirByCategory(DataCategory.CustomFile, session), filter);
    }

    private Collection<DataRecord> loadFrom(String root, final LoaderFilter<DataRecord> filter) {

        final List<DataRecord> out = new ArrayList<>();

        Iterable<File> iterable = com.google.common.io.Files.fileTreeTraverser().children(new File(root));

        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                CustomFileRecord record = new CustomFileRecord();
                record.setPath(file.getPath());
                record.setDisplayName(file.getName());
                record.setId(String.valueOf(file.hashCode()));
                record.setDir(file.isDirectory());
                if (!record.isDir()) try {
                    record.setSize(Files.asByteSource(file).size());
                } catch (IOException e) {
                    Logger.e("Failed to get file size", e.getLocalizedMessage());
                }
                if (filter == null || !filter.ignored(record)) out.add(record);
            }
        });

        return out;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
