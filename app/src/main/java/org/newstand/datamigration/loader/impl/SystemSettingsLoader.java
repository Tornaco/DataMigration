package org.newstand.datamigration.loader.impl;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;
import com.google.common.io.Files;
import com.google.gson.Gson;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.SettingsRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.tornaco.settingshook.SettingsItem;
import dev.tornaco.settingshook.SystemSettings;

/**
 * Created by Nick on 2017/6/21 13:10
 */

public class SystemSettingsLoader extends BaseLoader {

    @Override
    public String[] needPermissions() {
        return new String[0];
    }

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        final List<DataRecord> out = new ArrayList<>();
        if (RootManager.getInstance().obtainPermission() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Collections.consumeRemaining(SystemSettings.values(), new Consumer<SystemSettings>() {
                @Override
                public void accept(@NonNull final SystemSettings systemSettings) {
                    Collections.consumeRemaining(systemSettings.definations, new Consumer<SettingsItem>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void accept(@NonNull SettingsItem item) {
                            String value = getFromRoot(systemSettings.name().toLowerCase(), item.getKey());
                            if (value == null) return;
                            SettingsRecord sr = new SettingsRecord();
                            sr.setDisplayName(item.getName());
                            sr.setKey(item.getKey());
                            sr.setNamespace(systemSettings.name().toLowerCase());
                            sr.setValue(value);
                            out.add(sr);
                        }
                    });
                }
            });
        }
        return out;
    }

    private String getFromRoot(String namespace, String key) {
        String command = String.format("settings get %s %s", namespace, key);
        Result result = RootManager.getInstance().runCommand(command);
        String value = result.getMessage();
        Logger.d("getFromRoot, cmd:%s, value:%s", command, value);
        if (!(value.trim().equals("null"))) {
            return value;
        }
        return null;
    }

    @Override
    public Collection<DataRecord> loadFromSession(LoaderSource source, Session session, LoaderFilter<DataRecord> filter) {
        final List<DataRecord> out = new ArrayList<>();

        String dir = source.getParent() == LoaderSource.Parent.Received ?
                SettingsProvider.getReceivedDirByCategory(DataCategory.SystemSettings, session)
                : SettingsProvider.getBackupDirByCategory(DataCategory.SystemSettings, session);

        Iterable<File> iterable = Files.fileTreeTraverser().children(new File(dir));

        Collections.consumeRemaining(iterable, new Consumer<File>() {
            @Override
            public void accept(@NonNull File file) {
                try {
                    String content = org.newstand.datamigration.utils.Files.readString(file.getPath());
                    Gson gson = new Gson();
                    SettingsRecord sr = gson.fromJson(content, SettingsRecord.class);
                    sr.setPath(file.getPath());
                    sr.setChecked(false);
                    out.add(sr);
                } catch (Throwable e) {
                    Logger.e(e, "Fail load delegate:" + file);
                }
            }
        });

        return out;
    }
}
