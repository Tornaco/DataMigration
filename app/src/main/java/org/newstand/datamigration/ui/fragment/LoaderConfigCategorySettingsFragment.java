package org.newstand.datamigration.ui.fragment;

import android.support.annotation.NonNull;

import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.tiles.LoaderConfigCategoryTile;
import org.newstand.datamigration.utils.Collections;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick on 2017/6/21 17:05
 */

public class LoaderConfigCategorySettingsFragment extends DashboardFragment {


    @Override
    protected void onCreateDashCategories(final List<Category> categories) {
        super.onCreateDashCategories(categories);

        final Category pending = new Category();

        Collections.consumeRemaining(DataCategory.values(), new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                LoaderConfigCategoryTile configCategoryTile = new LoaderConfigCategoryTile(getContext(), category);
                pending.addTile(configCategoryTile);
            }
        });

        categories.add(pending);
    }
}
