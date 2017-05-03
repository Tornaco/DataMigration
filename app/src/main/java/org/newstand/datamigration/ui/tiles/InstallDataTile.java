package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.chrisplus.rootmanager.RootManager;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.logger.Logger;

import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/1 17:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class InstallDataTile extends ThemedTile {

    public InstallDataTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.tile_work_mode_root;
        this.summaryRes = R.string.summary_work_mode_root;
        this.iconRes = R.drawable.ic_work_mode;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.isInstallDataEnabled());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setInstallDataEnabled(checked);

                if (checked) {
                    SharedExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!RootManager.getInstance().obtainPermission()) {
                                Logger.d("Fail to request root perm, disable data installer.");
                                SettingsProvider.setInstallDataEnabled(false);
                            }
                        }
                    });
                }
            }
        };
    }
}
