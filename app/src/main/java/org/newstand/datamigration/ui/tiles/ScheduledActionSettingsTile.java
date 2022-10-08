package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.service.schedule.BackupActionSettings;

import dev.nick.tiles.tile.QuickTile;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledActionSettingsTile extends QuickTile {

    @Getter
    private BackupActionSettings actionSettings;

    public ScheduledActionSettingsTile(@NonNull Context context, BackupActionSettings settings) {
        super(context, null);
        this.actionSettings = settings;
    }
}
