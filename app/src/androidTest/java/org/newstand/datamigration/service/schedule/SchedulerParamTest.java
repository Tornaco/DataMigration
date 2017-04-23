package org.newstand.datamigration.service.schedule;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.repo.SchedulerParamRepoService;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.ArrayList;

/**
 * Created by Nick@NewStand.org on 2017/4/23 20:13
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class SchedulerParamTest {

    @Test
    public void toJson() {
        Condition condition = Condition.builder()
                .isPersisted(true)
                .requiresCharging(true)
                .requiresDeviceIdle(true)
                .triggerAtMills(System.currentTimeMillis())
                .triggerContentUris(ImmutableList.of("content://test", "content://test2"))
                .build();

        ScheduleAction action = ScheduleAction.builder()
                .actionType(ScheduleActionType.Backup)
                .settings(BackupActionSettings.builder()
                        .session(Session.from("test"))
                        .dataCategories(new ArrayList<DataCategory>(DataCategory.values().length))
                        .build()).build();

        SchedulerParam param = new SchedulerParam(condition, action);

        Gson g = new Gson();

        String json = g.toJson(param);

        Logger.d("j %s", json);

        Logger.d("f %s", g.fromJson(json, SchedulerParam.class));

        SchedulerParamRepoService.get().insert(InstrumentationRegistry.getTargetContext(), param);

        Logger.d(SchedulerParamRepoService.get().findFirst(InstrumentationRegistry.getTargetContext()));
    }
}