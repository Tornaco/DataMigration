package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import org.newstand.datamigration.policy.ExtraDataRule;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/3/28 10:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ExtraDataRulesRepoService extends GsonBasedRepoService<ExtraDataRule> {

    private static ExtraDataRulesRepoService sMe;

    public synchronized static ExtraDataRulesRepoService get() {
        if (sMe == null) {
            sMe = new ExtraDataRulesRepoService();
        }
        return sMe;
    }

    @Override
    protected Class<ExtraDataRule> getClz() {
        return ExtraDataRule.class;
    }

    @Override
    protected boolean isSame(ExtraDataRule old, ExtraDataRule now) {
        return old.getPackageName().equals(now.getPackageName());
    }

    @Override
    protected TypeToken onCreateTypeToken() {
        return new TypeToken<ArrayList<ExtraDataRule>>() {
        };
    }

    @Nullable
    public ExtraDataRule findByPkg(Context context, @NonNull String pkg) {
        List<ExtraDataRule> all = findAll(context);
        for (ExtraDataRule r : all) {
            if (r.getPackageName() == null) {
                Logger.e(new IllegalStateException("Bad rule, no pkg?"), "Ignore bad rule~");
                continue;
            }
            if (r.getPackageName().equals(pkg)) {
                return r;
            }
        }
        return null;
    }
}
