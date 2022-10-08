package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.service.schedule.Condition;

import dev.nick.tiles.tile.QuickTile;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/4/21 21:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ConditionItemTile extends QuickTile {
    @Getter
    Condition condition;

    public ConditionItemTile(@NonNull Context context, Condition condition) {
        super(context, null);
        this.condition = condition;
    }
}
