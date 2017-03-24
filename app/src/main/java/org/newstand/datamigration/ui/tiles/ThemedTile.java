package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.TileListener;

/**
 * Created by Nick@NewStand.org on 2017/3/23 17:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public abstract class ThemedTile extends QuickTile {

    public ThemedTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        onInitView(context);
    }

    abstract void onInitView(Context context);
}
