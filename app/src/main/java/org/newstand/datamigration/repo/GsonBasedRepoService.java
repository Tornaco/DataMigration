package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.utils.Files;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/28 12:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

abstract class GsonBasedRepoService<T> implements RepoService<T> {

    @Getter
    Gson gson = new Gson();

    @Getter
    protected String filePath;

    public GsonBasedRepoService() {
        filePath = SettingsProvider.getCommonDataDir() + File.separator + dataFileName();
        // Logger.v("Init repo service %s with path %s", getClass().getSimpleName(), filePath);
    }

    protected String dataFileName() {
        return getClass().getSimpleName().toLowerCase() + ".data";
    }

    @Override
    public boolean insert(Context context, @NonNull final T t) {
        List<T> all = findAll(context);
        if (!allowDupElements()) {
            Collections.consumeRemaining(all, new Consumer<T>() {
                @Override
                public void accept(@NonNull T c) {
                    if (isSame(t, c)) {
                        throw new IllegalArgumentException("Dup element:" + t);
                    }
                }
            });
        }
        all.add(t);
        String content = getGson().toJson(all);
        return Files.writeString(content, filePath);
    }

    protected boolean allowDupElements() {
        return false;
    }

    @Override
    public boolean delete(Context context, @NonNull T t) {
        List<T> all = findAll(context);
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            T e = all.get(i);
            if (isSame(e, t)) {
                index = i;
            }
        }
        if (index >= 0) {
            all.remove(index);
        } else {
            return false;
        }
        String content = getGson().toJson(all);
        return Files.writeString(content, filePath);
    }

    protected boolean isSame(T old, T now) {
        return false;
    }

    @Override
    public boolean update(Context context, @NonNull T t) {
        ArrayList<T> all = (ArrayList<T>) findAll(context);
        int index = -1;
        for (int i = 0; i < all.size(); i++) {
            T e = all.get(i);
            if (isSame(e, t)) {
                index = i;
            }
        }
        if (index >= 0) {
            all.remove(index);
            all.add(index, t);
        } else {
            return false;
        }
        String content = getGson().toJson(all);
        return Files.writeString(content, filePath);
    }

    @Override
    public boolean updateOrInsert(Context context, @NonNull T t) {
        return update(context, t) || insert(context, t);
    }

    @Override
    public T findFirst(Context context) {
        List<T> data = findAll(context);
        if (data.size() == 0) return null;
        return data.get(data.size() - 1);
    }

    @Override
    public T findLast(Context context) {
        List<T> data = findAll(context);
        if (data.size() == 0) return null;
        return data.get(0);
    }

    @Override
    public List<T> findAll(Context context) {
        String content = Files.readString(filePath);
        if (content == null) return new ArrayList<>();
        ArrayList<T> all = null;
        try {
            all = getGson().<ArrayList<T>>fromJson(content, onCreateTypeToken().getType());
        } catch (Throwable t) {
            Logger.e(t, "Fail fromJson");
        }
        if (all == null) all = new ArrayList<>();
        return all;
    }

    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<T>>() {
        };
    }

    @Override
    public int size(Context context) {
        List data = findAll(context);
        int size = data.size();
        data.clear();
        return size;
    }

    protected Class<T> getClz() {
        return null;
    }

    @Override
    public boolean drop() {
        return new File(filePath).delete();
    }
}
