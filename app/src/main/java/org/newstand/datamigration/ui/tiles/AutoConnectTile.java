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

public class AutoConnectTile extends ThemedTile {

    public AutoConnectTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.title_auto_connect;
        this.iconRes = R.drawable.ic_wifi_tethering;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.autoConnectEnabled());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setAutoConnectEnabled(checked);
            }
        };

    }
}
