package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/1 17:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AutoInstallTile extends ThemedTile {

    public AutoInstallTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.tile_auto_install;
        this.summaryRes = R.string.summaty_auto_install;
        this.iconRes = R.drawable.ic_accessible;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.isAutoInstallAppEnabled());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setAutoInstallAppEnabled(checked);
            }
        };
    }
}
