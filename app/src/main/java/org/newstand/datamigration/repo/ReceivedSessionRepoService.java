package org.newstand.datamigration.repo;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.worker.transport.Session;

import java.util.ArrayList;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ReceivedSessionRepoService extends GsonBasedRepoService<Session> {

    private static ReceivedSessionRepoService sMe;

    public synchronized static ReceivedSessionRepoService get() {
        if (sMe == null) {
            sMe = new ReceivedSessionRepoService();
        }
        return sMe;
    }

    @Override
    protected Class<Session> getClz() {
        return Session.class;
    }

    @Override
    protected boolean isSame(Session old, Session now) {
        return old.getDate() == now.getDate();
    }

    @Override
    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<Session>>() {
        };
    }
}
