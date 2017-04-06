package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.chrisplus.rootmanager.RootManager;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.strategy.WorkMode;
import org.newstand.datamigration.sync.SharedExecutor;

import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/1 17:04
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WorkModeTile extends ThemedTile {

    public WorkModeTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {
        this.titleRes = R.string.tile_work_mode_root;
        this.iconRes = R.drawable.ic_work_mode;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.workMode() == WorkMode.ROOT);
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                WorkMode mode = SettingsProvider.workMode();
                SettingsProvider.setWorkMode(mode == WorkMode.ROOT ? WorkMode.NORMAL : WorkMode.ROOT);
                mode = SettingsProvider.workMode();

                if (mode == WorkMode.ROOT) {
                    SharedExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            RootManager.getInstance().obtainPermission();
                        }
                    });
                }
            }
        };
    }
}
