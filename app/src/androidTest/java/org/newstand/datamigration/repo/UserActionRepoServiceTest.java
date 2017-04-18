package org.newstand.datamigration.repo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.datamigration.utils.Files;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/4/18 14:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class UserActionRepoServiceTest {
    @Test
    public void all() throws Exception {

        Context context = InstrumentationRegistry.getTargetContext();

        List<UserAction> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserAction userAction = UserAction.builder()
                    .date(System.currentTimeMillis())
                    .fingerPrint(System.currentTimeMillis())
                    .eventTitle("test" + i)
                    .eventDescription("test")
                    .build();
            list.add(userAction);

            UserActionRepoService.get().insert(context, userAction);
        }

        Gson gson = new Gson();
        String json = gson.toJson(list);
        Logger.d("json %s", json);

        String path = SettingsProvider.getTestDir() + File.separator + "test.data";
        boolean ok = Files.writeString(json, path);
        Logger.d("write %s", ok);

        String read = Files.readString(path);
        Logger.d("read %s", read);

        Gson gson1 = new Gson();
        ArrayList actions = gson1.fromJson(read, ArrayList.class);

        for (Object action : actions) {
            Logger.d(action);
        }

        // Test start
        Logger.d(UserActionRepoService.get().size(context));
        Logger.d(UserActionRepoService.get().delete(context, UserAction.builder()
                .date(System.currentTimeMillis())
                .fingerPrint(System.currentTimeMillis())
                .eventTitle("test" + 1)
                .eventDescription("test")
                .build()));
        Logger.d(UserActionRepoService.get().delete(context, list.get(1)));
        UserAction updateAction = list.get(1);
        updateAction.setEventDescription("UPDATED");
        Logger.d(UserActionRepoService.get().update(context, updateAction));
        Logger.d(UserActionRepoService.get().findFirst(context));
        Logger.d(UserActionRepoService.get().findLast(context));

        Sleeper.sleepQuietly(Interval.Minutes.getIntervalMills());
    }

}