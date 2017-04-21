package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.service.schedule.ActionSettings;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledBackupActionSettingsTile extends ScheduledActionSettingsTile {
    public ScheduledBackupActionSettingsTile(@NonNull Context context, ActionSettings settings) {
        super(context, settings);
    }
}
