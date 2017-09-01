package org.newstand.datamigration.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.newstand.datamigration.R;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.tiles.CheckForUpdateTile;
import org.newstand.datamigration.ui.tiles.LicenceTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.utils.SeLinuxEnabler;
import org.newstand.datamigration.utils.SeLinuxState;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/14 17:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AboutActivity extends TransitionSafeActivity {
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

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.settings, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }


        public static SettingsFragment getInstance() {
            return new SettingsFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {

            Category about = new ThemedCategory();
            about.titleRes = R.string.tile_category_about;

            CheckForUpdateTile checkForUpdateTile = new CheckForUpdateTile(getActivity());
            about.addTile(checkForUpdateTile);
            LicenceTile licenceTile = new LicenceTile(getActivity());
            about.addTile(licenceTile);

            categories.add(about);

            super.onCreateDashCategories(categories);
        }
    }
}
