package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.service.schedule.Condition;

import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 21:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ConditionChargeTile extends ConditionItemTile {

    public ConditionChargeTile(@NonNull Context context, final Condition condition) {
        super(context, condition);

        this.titleRes = R.string.title_condition_charge;
        this.iconRes = R.drawable.ic_power;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(condition.isRequiresCharging());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                condition.setRequiresCharging(checked);
            }
        };
    }
}
