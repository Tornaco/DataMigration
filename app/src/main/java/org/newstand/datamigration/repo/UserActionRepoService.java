package org.newstand.datamigration.repo;

import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;

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
        return Realm.getInstance(new RealmConfiguration.Builder()
                .directory(new File(SettingsProvider.getCommonDataDir()))
                .name("user_actions")
                .build());
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
        return res;
    }

    @Override
    UserAction map(UserAction action) {
        return UserAction.from(action);
    }
}
