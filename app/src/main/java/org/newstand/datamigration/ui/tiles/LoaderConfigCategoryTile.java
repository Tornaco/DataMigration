package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.provider.SettingsProvider;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick on 2017/6/21 17:02
 */

public class LoaderConfigCategoryTile extends QuickTile {

    private DataCategory category;

    public LoaderConfigCategoryTile(@NonNull Context context, DataCategory category) {
        super(context, null);
        this.title = context.getString(category.nameRes());
        this.iconRes = category.iconRes();
        this.category = category;

        onInitView(context);
    }

    void onInitView(Context context) {
        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.isLoadEnabledForCategory(category));
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setLoadEnabledForCategory(category, checked);
            }
        };
    }
}
