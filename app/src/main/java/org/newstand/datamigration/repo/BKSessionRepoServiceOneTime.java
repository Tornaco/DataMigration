package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;

import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Files;
import org.newstand.datamigration.worker.backup.session.Session;

import java.io.File;

import io.realm.Realm;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BKSessionRepoServiceOneTime extends OneTimeRealmRepoService<Session> {

    private static BKSessionRepoServiceOneTime sMe;

    private BKSessionRepoServiceOneTime() {
        super();
    }

    public static synchronized BKSessionRepoServiceOneTime get() {
        if (sMe == null) sMe = new BKSessionRepoServiceOneTime();
        return sMe;
    }

    @Override
    protected Class<Session> clz() {
        return Session.class;
    }

    @Override
    Session findSame(Realm r, Session session) {
        return r.where(Session.class).equalTo("name", session.getName())
                .equalTo("date", session.getDate()).findFirst();
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
