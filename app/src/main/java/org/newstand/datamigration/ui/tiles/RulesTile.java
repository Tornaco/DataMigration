package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.ExtraRulesViewerActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RulesTile extends ThemedTile {

    public RulesTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.action_extra_data_rules_viewer;
        this.iconRes = R.drawable.ic_smoke;
        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                Intent intent = new Intent(getContext(), ExtraRulesViewerActivity.class);
                transitionSafeActivity.transitionTo(intent);
            }
        };
    }
}
