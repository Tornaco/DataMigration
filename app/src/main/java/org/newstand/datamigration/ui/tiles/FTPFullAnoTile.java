package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;

import be.ppareit.swiftp.FsSettings;
import dev.nick.tiles.tile.SwitchTileView;


/**
 * Created by Nick on 2017/6/26 16:36
 */

public class FTPFullAnoTile extends ThemedTile {

    public FTPFullAnoTile(@NonNull Context context) {
        super(context, null);
        this.titleRes = R.string.anonymous_label;
        this.iconRes = R.drawable.ic_accessible;
    }

    @Override
    void onInitView(Context context) {
        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(FsSettings.allowAnoymous());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                FsSettings.setAllowAnoymous(checked);
            }
        };
    }
}
