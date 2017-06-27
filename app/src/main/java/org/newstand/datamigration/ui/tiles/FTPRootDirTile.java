package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;

import be.ppareit.swiftp.FsSettings;
import dev.nick.tiles.tile.EditTextTileView;


/**
 * Created by Nick on 2017/6/26 16:36
 */

public class FTPRootDirTile extends ThemedTile {

    public FTPRootDirTile(@NonNull Context context) {
        super(context, null);
        this.titleRes = R.string.chroot_label;
        this.iconRes = R.drawable.ic_files;
    }

    @Override
    void onInitView(final Context context) {
        this.tileView = new EditTextTileView(context) {
            @Override
            protected CharSequence getHint() {
                return FsSettings.getChrootDirAsString();
            }

            @Override
            protected CharSequence getDialogTitle() {
                return context.getString(R.string.chroot_label);
            }

            @Override
            protected CharSequence getPositiveButton() {
                return context.getString(android.R.string.ok);
            }

            @Override
            protected CharSequence getNegativeButton() {
                return context.getString(android.R.string.cancel);
            }

            @Override
            protected void onPositiveButtonClick() {
                super.onPositiveButtonClick();

                String dir = getEditText().getText().toString();
            }
        };
    }
}
