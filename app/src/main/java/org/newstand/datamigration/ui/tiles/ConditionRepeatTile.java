package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.service.schedule.Condition;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ConditionRepeatTile extends QuickTile {

    public ConditionRepeatTile(@NonNull final Context context, final Condition condition) {
        super(context, null);

        this.titleRes = R.string.title_settings_repeat;
        this.iconRes = R.drawable.ic_repeat;
        this.summaryRes = R.string.summary_settings_repeat;


        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(condition.isRepeat());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                condition.setRepeat(checked);
            }
        };
    }
}
