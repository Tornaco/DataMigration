package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class QQGroupTile extends ThemedTile {

    public QQGroupTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_qq_group;
        this.summary = getContext().getString(R.string.summary_qq_group, "468297384");
        this.iconRes = R.drawable.ic_whatshot;

        this.tileView = new QuickTileView(getContext(), this);
    }

}
