package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.utils.Closer;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Nick@NewStand.org on 2017/3/28 12:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class OneTimeRealmRepoService<T extends RealmObject> implements RepoService<T> {

    @Nullable
    Realm getRealm() {
        return null;
    }

    protected abstract Class<T> clz();

    @Override
    public boolean insert(@NonNull final T t) {
        Realm r = getRealm();
        if (r == null) {
            return false;
        }
        r.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(t);
            }
        });
        Closer.closeQuietly(r);
        return true;
    }

    @Override
    public boolean delete(@NonNull final T t) {
        final boolean[] res = {false};
        Realm realm = getRealm();
        if (realm == null) {
            return false;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                T toBe = findSame(realm, t);
                if (toBe != null) {
                    toBe.deleteFromRealm();
                    res[0] = true;
                } else {
                    res[0] = false;
                }
            }
        });
        Closer.closeQuietly(realm);
        return res[0];
    }

    abstract T findSame(Realm r, T t);

    @Override
    public boolean update(@NonNull T t) {
        return false;
    }

    @Override
    public T findFirst() {
        Realm r = getRealm();
        if (r == null) {
            return null;
        }
        T f = r.where(clz()).findFirst();
        T res = map(f);
        Closer.closeQuietly(r);
        return res;
    }

    @Override
    public T findLast() {
        Realm r = getRealm();
        if (r == null) {
            return null;
        }
        T res = null;
        RealmResults<T> ts = r.where(clz()).findAll();
        if (!Collections.isNullOrEmpty(ts)) {
            res = map(ts.last());
        }
        Closer.closeQuietly(r);
        return res;
    }

    abstract T map(T t);

    @Override
    public List<T> findAll() {
        Realm r = getRealm();
        if (r == null) {
            return java.util.Collections.emptyList();
        }
        List<T> ts = r.where(clz()).findAll();
        final List<T> res = new ArrayList<>();
        if (!Collections.isNullOrEmpty(ts)) {
            Collections.consumeRemaining(ts, new Consumer<T>() {
                @Override
                public void accept(@NonNull T t) {
                    res.add(map(t));
                }
            });
        }
        Closer.closeQuietly(r);
        return res;
    }

    @Override
    public int size() {
        Realm r = getRealm();
        if (r == null) {
            return 0;
        }
        List<T> ts = r.where(clz()).findAll();
        int size = ts == null ? 0 : ts.size();
        Closer.closeQuietly(r);
        return size;
    }
}
