package org.newstand.datamigration.service.schedule;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;

import org.newstand.datamigration.common.ContextWireable;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@ToString
public class ScheduleAction {
    private ScheduleActionType actionType;
    private ActionSettings settings;

    public int execute(Context context) {
        //noinspection unchecked
        ActionExecutor<ActionSettings> executor = actionType.produce();
        if (executor instanceof ContextWireable) {
            ((ContextWireable) executor).wire(context);
        }
        return executor.execute(settings);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PersistableBundle toBundle() {
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString("actionType", actionType.name());

        settings.inflateIntoBundle(persistableBundle);

        return persistableBundle;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScheduleAction fromBundle(PersistableBundle bundle) {
        ScheduleActionType actionType = ScheduleActionType.valueOf(bundle.getString("actionType"));
        ActionSettings settings = actionType.createEmptySettings();
        return ScheduleAction.builder()
                .actionType(actionType)
                .settings(settings.fromBundle(bundle))
                .build();
    }
}
