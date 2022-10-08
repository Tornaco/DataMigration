package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.SeLinuxEnabler;
import org.newstand.datamigration.utils.SeLinuxState;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/3/15 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SeLinuxTile extends ThemedTile {

    public SeLinuxTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        final SeLinuxState seLinuxState = SeLinuxState.Unknown;

        loadState();

        if (SettingsProvider.isTipsNoticed("selinux_tap_to_disable")) {
            this.summary = getContext().getString(seLinuxState.nameRes()) + "\t" + getContext().getString(R.string.summary_selinux_state);
        } else {
            this.summary = getContext().getString(seLinuxState.nameRes());
        }


        this.titleRes = R.string.selinux_state;
        this.iconRes = R.drawable.ic_secure;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                SharedExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (SeLinuxEnabler.setState(SeLinuxState.Permissive)) {
                            loadState();
                        }
                        SettingsProvider.setTipsNoticed("selinux_tap_to_disable", true);
                    }
                });
            }
        };
    }

    private void loadState() {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final SeLinuxState s = SeLinuxEnabler.getSeLinuxState();
                SharedExecutor.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSummary(s);
                    }
                });
            }
        });
    }

    private void updateSummary(SeLinuxState seLinuxState) {
        if (SettingsProvider.isTipsNoticed("selinux_tap_to_disable") && seLinuxState == SeLinuxState.Enforcing) {
            this.summary = getContext().getString(seLinuxState.nameRes()) + "\t" + getContext().getString(R.string.summary_selinux_state);
        } else {
            this.summary = getContext().getString(seLinuxState.nameRes());
        }
        getTileView().getSummaryTextView().setText(this.summary);
    }
}
