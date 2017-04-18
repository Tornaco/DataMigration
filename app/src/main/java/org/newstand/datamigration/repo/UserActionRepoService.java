package org.newstand.datamigration.repo;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.data.event.UserAction;

import java.util.ArrayList;

/**
 * Created by Nick@NewStand.org on 2017/3/29 16:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionRepoService extends GsonBasedRepoService<UserAction> {

    private static UserActionRepoService sMe;

    public synchronized static UserActionRepoService get() {
        if (sMe == null) {
            sMe = new UserActionRepoService();
        }
        return sMe;
    }

    @Override
    protected Class<UserAction> getClz() {
        return UserAction.class;
    }

    @Override
    protected boolean matchCase(UserAction old, UserAction now) {
        return old.getFingerPrint() == now.getFingerPrint();
    }

    @Override
    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<UserAction>>() {
        };
    }
}
