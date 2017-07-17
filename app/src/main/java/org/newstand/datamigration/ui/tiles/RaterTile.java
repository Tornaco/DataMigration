package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RaterTile extends ThemedTile {

    public RaterTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(final Context context) {

        this.titleRes = R.string.title_rate;
        this.summaryRes = R.string.summary_rate;
        this.iconRes = R.drawable.ic_stars;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
            }
        };
    }

}
