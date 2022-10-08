package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.widget.StaticColorQuickTileView;
import org.newstand.datamigration.utils.Files;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ShareTile extends ThemedTile {

    public ShareTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_transport_share;
        this.iconRes = R.drawable.ic_menu_share;
        this.tileView = new StaticColorQuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                onRequestShare();
            }
        };
    }

    private void onRequestShare() {
        if (!SettingsProvider.isTipsNoticed("onRequestShare"))
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.title_transport_share)
                    .setMessage(R.string.title_transport_share_tips)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(getContext());
                        }
                    })
                    .setNeutralButton(R.string.title_never_remind, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingsProvider.setTipsNoticed("onRequestShare", true);
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(getContext());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .show();
        else Files.shareDateMigrationAsync(getContext());
    }

}
