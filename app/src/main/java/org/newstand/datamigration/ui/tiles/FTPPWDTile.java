package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;

import be.ppareit.swiftp.FsSettings;
import dev.nick.tiles.tile.QuickTileView;


/**
 * Created by Nick on 2017/6/26 16:36
 */

public class FTPPWDTile extends ThemedTile {

    public FTPPWDTile(@NonNull Context context) {
        super(context, null);
        this.titleRes = R.string.password_label;
        this.summary = FsSettings.getPassWord();
        this.iconRes = R.drawable.ic_secure;
    }

    @Override
    void onInitView(Context context) {
        this.tileView = new QuickTileView(context, this);
    }
}
