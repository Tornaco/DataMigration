package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.ui.widget.YMTileView;

import dev.nick.tiles.tile.QuickTile;

/**
 * Created by Nick@NewStand.org on 2017/5/2 16:55
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class YMTile extends QuickTile {

    public YMTile(@NonNull Context context) {
        super(context, null);
        this.tileView = new YMTileView(context);
    }
}
