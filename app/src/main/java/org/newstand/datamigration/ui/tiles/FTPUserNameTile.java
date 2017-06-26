package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;

import be.ppareit.swiftp.FsSettings;
import dev.nick.tiles.tile.QuickTileView;


/**
 * Created by Nick on 2017/6/26 16:36
 */

public class FTPUserNameTile extends ThemedTile {

    public FTPUserNameTile(@NonNull Context context) {
        super(context, null);
        this.titleRes = R.string.username_label;
        this.summary = FsSettings.getUserName();
        this.iconRes = R.drawable.ic_contacts;
    }

    @Override
    void onInitView(Context context) {
        this.tileView = new QuickTileView(context, this);
    }
}
