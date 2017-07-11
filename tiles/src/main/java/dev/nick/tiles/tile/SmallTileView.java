package dev.nick.tiles.tile;

import android.content.Context;
import android.util.AttributeSet;

import dev.nick.tiles.R;

public class SmallTileView extends TileView {
    public SmallTileView(Context context) {
        super(context);
    }

    public SmallTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dashboard_tile_small;
    }
}
