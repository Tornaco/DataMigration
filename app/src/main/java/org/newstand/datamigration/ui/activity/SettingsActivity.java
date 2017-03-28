package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.tiles.AutoConnectTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/14 17:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SettingsActivity extends TransitionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SettingsFragment.getInstance()).commit();
    }

    public static class SettingsFragment extends DashboardFragment {
        public static SettingsFragment getInstance() {
            return new SettingsFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {

            Category storage = new ThemedCategory();
            storage.titleRes = R.string.tile_category_storage;

            AutoConnectTile autoConnectTile = new AutoConnectTile(getContext());
            storage.addTile(autoConnectTile);

            categories.add(storage);

            super.onCreateDashCategories(categories);
        }
    }
}
