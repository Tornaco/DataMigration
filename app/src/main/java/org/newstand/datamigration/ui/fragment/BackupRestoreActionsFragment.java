package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.ui.tiles.BackupTile;
import org.newstand.datamigration.ui.tiles.RestoreTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 11:47
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupRestoreActionsFragment extends DashboardFragment {

    public static BackupRestoreNavigatorFragment create() {
        return new BackupRestoreNavigatorFragment();
    }

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);

        Category about = new ThemedCategory();
        about.addTile(new BackupTile(getActivity()));
        about.addTile(new RestoreTile(getActivity()));

        categories.add(about);
    }
}
