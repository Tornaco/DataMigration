package org.newstand.datamigration.service.schedule;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import org.newstand.datamigration.common.ContextWireable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

/**
 * Created by Nick@NewStand.org on 2017/4/21 17:57
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Builder
@Getter
@Setter
@ToString
public class ScheduleAction {

    private long id;
    private ScheduleActionType actionType;
    private BackupActionSettings settings;

    public int execute(Context context) {
        //noinspection unchecked
        ActionExecutor<BackupActionSettings> executor = actionType.produce();
        if (executor instanceof ContextWireable) {
            ((ContextWireable) executor).wire(context);
        }
        return executor.execute(settings);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("actionType", actionType.name());

        settings.inflateIntoBundle(bundle);

        return bundle;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PersistableBundle toPersistBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("actionType", actionType.name());

        settings.inflateIntoBundle(bundle);

        return bundle;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScheduleAction fromBundle(BaseBundle bundle) {
        ScheduleActionType actionType = ScheduleActionType.valueOf(bundle.getString("actionType"));
        BackupActionSettings settings = actionType.createEmptySettings();
        return ScheduleAction.builder()
                .actionType(actionType)
                .settings(settings.fromBundle(bundle))
                .build();
    }
}
