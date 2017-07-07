package org.newstand.datamigration.service.schedule;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.ImmutableList;

import org.junit.runner.RunWith;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.worker.transport.Session;

/**
 * Created by Nick@NewStand.org on 2017/4/21 18:06
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class ScheduleActionTest {
    public void testBuilder() {
        ScheduleAction scheduleAction = ScheduleAction.builder()
                .actionType(ScheduleActionType.Backup)
                .settings(BackupActionSettings.builder()
                        .session(Session.create())
                        .dataCategories(ImmutableList.of(DataCategory.CallLog, DataCategory.Contact, DataCategory.Sms)).build())
                .build();

        scheduleAction.execute(InstrumentationRegistry.getTargetContext());
    }
}