package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.InstallerTimeout;
import org.newstand.datamigration.provider.SettingsProvider;

import java.util.ArrayList;
import java.util.List;

import dev.nick.tiles.tile.DropDownTileView;

/**
 * Created by Nick@NewStand.org on 2017/3/15 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class InstallerTimeoutTile extends ThemedTile {

    public InstallerTimeoutTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        final InstallerTimeout current = SettingsProvider.getAppInstallerTimeout();

        this.summary = context.getString(R.string.summery_install_timeout, String.valueOf(current.timeMills));

        final List<InstallerTimeout> allTimes = new ArrayList<>();

        final List<String> allName = new ArrayList<>();
        org.newstand.datamigration.utils.Collections.consumeRemaining(InstallerTimeout.values(),
                new Consumer<InstallerTimeout>() {
                    @Override
                    public void accept(@NonNull InstallerTimeout timeout) {
                        allTimes.add(timeout);
                        allName.add(String.valueOf(timeout.timeMills));
                    }
                });

        this.titleRes = R.string.tile_install_timeout;
        this.iconRes = R.drawable.ic_timer;

        this.tileView = new DropDownTileView(getContext()) {

            @Override
            protected int getInitialSelection() {
                return current.ordinal();
            }

            @Override
            protected List<String> onCreateDropDownList() {
                return allName;
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                InstallerTimeout timeout = allTimes.get(position);
                if (timeout == SettingsProvider.getAppInstallerTimeout()) {
                    return;
                }
                SettingsProvider.setAppInstallerTimeout(timeout);
                updateSummary();
            }
        };
    }

    private void updateSummary() {
        InstallerTimeout current = SettingsProvider.getAppInstallerTimeout();
        this.summary = getContext().getString(R.string.summery_install_timeout,
                String.valueOf(current.timeMills));
        getTileView().getSummaryTextView().setText(this.summary);
    }
}
