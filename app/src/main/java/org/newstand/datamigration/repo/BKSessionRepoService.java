package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bugsnag.android.Bugsnag;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Files;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BKSessionRepoService extends OneTimeRealmRepoService<Session> {

    private static BKSessionRepoService sMe;

    private BKSessionRepoService() {
        super();
    }

    public static synchronized BKSessionRepoService get() {
        if (sMe == null) sMe = new BKSessionRepoService();
        return sMe;
    }

    @Override
    @Nullable
    Realm getRealm() {
        Realm r = null;
        try {
            r = Realm.getInstance(new RealmConfiguration.Builder()
                    .directory(new File(SettingsProvider.getBackupRootDir()))
                    .name("backup_sessions")
                    .build());
        } catch (Throwable t) {
            Logger.e(t, "Fail to get realm");
        }
        return r;
    }

    @Override
    protected Class<Session> clz() {
        return Session.class;
    }

    @Override
    public boolean update(@NonNull final Session session) {
        Logger.d("update %s", session);
        final boolean[] res = {false};
        final Realm r = getRealm();
        if (r == null) return false;
        r.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Session old = findSame(r, session);
                if (old == null) return;
                old.setName(session.getName());
                res[0] = true;
            }
        });
        return res[0];
    }

    @Override
    Session findSame(Realm r, Session session) {
        return r.where(Session.class).equalTo("date", session.getDate()).findFirst();
    }

    @Override
    Session map(Session session) {
        return Session.from(session);
    }

    @Override
    public boolean delete(@NonNull Session session) {
        boolean ok = super.delete(session);
        if (ok) {
            File targetFile = new File(SettingsProvider.getBackupSessionDir(session));
            ok = Files.deleteDir(targetFile);
        }
        return ok;
    }
}
