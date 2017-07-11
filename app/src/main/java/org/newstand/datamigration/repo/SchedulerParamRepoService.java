package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.service.schedule.SchedulerParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SchedulerParamRepoService extends GsonBasedRepoService<SchedulerParam> {

    private static SchedulerParamRepoService sMe;

    public synchronized static SchedulerParamRepoService get() {
        if (sMe == null) {
            sMe = new SchedulerParamRepoService();
        }
        return sMe;
    }

    @Override
    protected Class<SchedulerParam> getClz() {
        return SchedulerParam.class;
    }

    @Override
    protected boolean isSame(SchedulerParam old, SchedulerParam now) {
        return old.getAction().getId() == now.getAction().getId();
    }

    @Override
    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<SchedulerParam>>() {
        };
    }

    @Nullable
    public SchedulerParam findById(Context c, long id) {
        List<SchedulerParam> all = findAll(c);
        for (SchedulerParam p : all) {
            if (id == p.getAction().getId())
                return p;
        }
        return null;
    }
}
