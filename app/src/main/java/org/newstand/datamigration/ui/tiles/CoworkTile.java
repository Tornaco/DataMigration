package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.R;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CoworkTile extends ThemedTile {

    public CoworkTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_cowork;
        this.summary = getContext().getString(R.string.summary_cowork);
        this.iconRes = R.drawable.ic_group_work;

        this.tileView = new QuickTileView(getContext(), this);
    }

}
