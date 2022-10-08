package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import dev.nick.tiles.tile.DropDownTileView;

/**
 * Created by Nick@NewStand.org on 2017/3/15 11:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ThemeColorTile extends ThemedTile {

    public ThemeColorTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        final ThemeColor current = SettingsProvider.getThemeColor();

        this.summary = getContext().getString(current.nameRes());

        final List<ThemeColor> allColor = new ArrayList<>();

        final List<String> allName = new ArrayList<>();
        org.newstand.datamigration.utils.Collections.consumeRemaining(ThemeColor.values(),
                new Consumer<ThemeColor>() {
                    @Override
                    public void accept(@NonNull ThemeColor themeColor) {
                        allColor.add(themeColor);
                        allName.add(getContext().getString(themeColor.nameRes()));
                    }
                });

        this.titleRes = R.string.tile_theme_color;
        this.iconRes = R.drawable.ic_theme;

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
                ThemeColor color = allColor.get(position);

                Logger.v("on color select current-%s, color-%s", current, color);

                if (color == current) {
                    return;
                }

                SettingsProvider.setAppThemeColor(color);
                updateSummary();
                showTips();
            }
        };
    }

    private void showTips() {
        Snackbar.make(getTileView(), R.string.tips_theme_color, Snackbar.LENGTH_LONG).show();
    }

    private void updateSummary() {
        ThemeColor current = SettingsProvider.getThemeColor();
        this.summary = getContext().getString(current.nameRes());
        getTileView().getSummaryTextView().setText(this.summary);
    }
}
