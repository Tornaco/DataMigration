package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.LicenseViewerActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 15:56
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LicenceTile extends ThemedTile {

    public LicenceTile(@NonNull final Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.title_license;
        this.iconRes = R.drawable.ic_info;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                transitionSafeActivity.transitionTo(new Intent(getContext(), LicenseViewerActivity.class));
            }
        };
    }
}
