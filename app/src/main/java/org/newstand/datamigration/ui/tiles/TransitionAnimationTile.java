package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/3/15 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class TransitionAnimationTile extends ThemedTile {

    public TransitionAnimationTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.tile_transition_animation;
        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.isTransitionAnimationEnabled());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setTransitionAnimationEnabled(checked);
            }
        };

    }
}
