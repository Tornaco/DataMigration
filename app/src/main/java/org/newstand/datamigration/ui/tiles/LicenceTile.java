package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.LicenseViewerActivity;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.nick.tiles.tile.TileListener;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:56
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LicenceTile extends ThemedTile {

    public LicenceTile(@NonNull final Context context) {
        super(context, new TileListener() {
            @Override
            public void onTileClick(@NonNull QuickTile tile) {
                context.startActivity(new Intent(context, LicenseViewerActivity.class));
            }
        });
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.title_license;
        this.iconRes = R.drawable.ic_work_mode;

        this.tileView = new QuickTileView(getContext(), this);
    }
}
