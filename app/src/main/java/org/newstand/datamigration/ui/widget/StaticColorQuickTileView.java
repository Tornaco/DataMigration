package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/5/3 9:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class StaticColorQuickTileView extends QuickTileView {

    public StaticColorQuickTileView(Context context, QuickTile tile) {
        super(context, tile);
    }

    public StaticColorQuickTileView(Context context, AttributeSet attrs, QuickTile tile) {
        super(context, attrs, tile);
    }

    @Override
    protected boolean useStaticTintColor() {
        return true;
    }
}
