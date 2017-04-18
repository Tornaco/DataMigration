package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;

import com.bugsnag.android.Bugsnag;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Closer;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionRepoService extends OneTimeRealmRepoService<UserAction> {

    private static UserActionRepoService sMe;

    private UserActionRepoService() {
        super();
    }

    public static synchronized UserActionRepoService get() {
        if (sMe == null) sMe = new UserActionRepoService();
        return sMe;
    }

    @Override
    Realm getRealm() {
        Realm r = null;
        try {
            r = Realm.getInstance(new RealmConfiguration.Builder()
                    .directory(new File(SettingsProvider.getCommonDataDir()))
                    .name("user_actions")
                    .build());
        } catch (Throwable e) {
            Logger.e(e, "Fail to get realm");
        }
        return r;
    }

    @Override
    protected Class<UserAction> clz() {
        return UserAction.class;
    }

    @Override
    UserAction findSame(Realm r, UserAction action) {
        return r.where(UserAction.class).equalTo("fingerPrint", action.getFingerPrint()).findFirst();
    }

    public List<UserAction> findByFingerPrint(long fingerPrint) {
        Realm r = getRealm();
        if (r == null) return java.util.Collections.emptyList();
        r.beginTransaction();
        List<UserAction> all = r.where(UserAction.class)
                .equalTo("fingerPrint", fingerPrint).findAll();
        final List<UserAction> res = new ArrayList<>();
        Collections.consumeRemaining(all, new Consumer<UserAction>() {
            @Override
            public void accept(@NonNull UserAction action) {
                res.add(UserAction.from(action));
            }
        });
        r.commitTransaction();
        Closer.closeQuietly(r);
        return res;
    }

    @Override
    UserAction map(UserAction action) {
        return UserAction.from(action);
    }
}
