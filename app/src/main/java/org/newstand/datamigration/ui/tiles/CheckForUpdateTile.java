package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CheckForUpdateTile extends ThemedTile {

    public CheckForUpdateTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_check_for_update;
        this.iconRes = R.drawable.ic_update;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                checkForUpdate();
            }
        };
    }

    private void checkForUpdate() {
        VersionRetriever.hasLaterVersionAsync(getContext(), new Consumer<VersionCheckResult>() {
            @Override
            public void accept(@NonNull VersionCheckResult versionCheckResult) {
                if (versionCheckResult.isHasLater()) {
                    VersionInfoDialog.attach(getContext(), versionCheckResult.getVersionInfo());
                } else {
                    Snackbar.make(getTileView(), "Already laters", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
