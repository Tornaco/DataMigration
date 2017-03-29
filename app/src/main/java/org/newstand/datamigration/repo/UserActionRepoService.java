package org.newstand.datamigration.repo;

import org.newstand.datamigration.data.event.UserAction;

import io.realm.Realm;

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
    protected Class<UserAction> clz() {
        return UserAction.class;
    }

    @Override
    UserAction findSame(Realm r, UserAction action) {
        return r.where(UserAction.class).equalTo("fingerPrint", action.getFingerPrint()).findFirst();
    }

    @Override
    UserAction map(UserAction action) {
        return UserAction.from(action);
    }
}
