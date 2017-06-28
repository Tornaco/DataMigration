package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.tiles.LoaderConfigCategoryTile;

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

        Category priv = new Category();
        priv.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Contact));
        priv.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.CallLog));
        priv.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Sms));
        priv.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Alarm));

        Category mm = new Category();
        mm.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Music));
        mm.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Video));
        mm.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Photo));

        Category conf = new Category();
        conf.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.Wifi));
        conf.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.App));
        conf.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.SystemApp));
        conf.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.SystemSettings));
        conf.addTile(new LoaderConfigCategoryTile(getContext(), DataCategory.CustomFile));

        categories.add(priv);
        categories.add(mm);
        categories.add(conf);
    }
}
