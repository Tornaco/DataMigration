package org.newstand.datamigration.service.schedule;

import android.annotation.TargetApi;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.List;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 18:05
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@ToString
public class BackupActionSettings extends ActionSettings {

    private List<DataCategory> dataCategories;
    private Session session;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void inflateIntoBundle(PersistableBundle bundle) {

        String[] categoryArray = new String[dataCategories.size()];

        for (int i = 0; i < dataCategories.size(); i++) {
            categoryArray[i] = dataCategories.get(i).name();
        }

        bundle.putStringArray("dataCategories", categoryArray);

        bundle.putString("session", new Gson().toJson(session));
    }

    public void inflateIntoBundle(Bundle bundle) {

        String[] categoryArray = new String[dataCategories.size()];

        for (int i = 0; i < dataCategories.size(); i++) {
            categoryArray[i] = dataCategories.get(i).name();
        }

        bundle.putStringArray("dataCategories", categoryArray);

        bundle.putString("session", new Gson().toJson(session));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BackupActionSettings fromBundle(BaseBundle bundle) {

        String[] categoryArray = bundle.getStringArray("dataCategories");

        Collections.consumeRemaining(categoryArray, new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) {
                dataCategories.add(DataCategory.valueOf(s));
            }
        });

        session = new Gson().fromJson(bundle.getString("session"), Session.class);

        Logger.d("Build session with %s, out %s", bundle.get("session"), session);

        return this;
    }
}
