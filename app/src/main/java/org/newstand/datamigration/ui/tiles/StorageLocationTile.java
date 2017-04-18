package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class StorageLocationTile extends ThemedTile {

    public StorageLocationTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.title_storage_location;
        this.summary = getContext().getString(R.string.summary_storage_location, SettingsProvider.getCommonRootDir());
        this.iconRes = R.drawable.ic_sd;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
            }
        };
    }

}
