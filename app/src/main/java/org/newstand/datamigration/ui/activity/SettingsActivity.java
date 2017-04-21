package org.newstand.datamigration.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.tiles.AutoInstallTile;
import org.newstand.datamigration.ui.tiles.DevTile;
import org.newstand.datamigration.ui.tiles.EncryptTile;
import org.newstand.datamigration.ui.tiles.InstallDataTile;
import org.newstand.datamigration.ui.tiles.StorageLocationTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.tiles.TransitionAnimationTile;
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

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.settings, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_disable_selinux) {
                SharedExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        SeLinuxEnabler.setState(SeLinuxState.Permissive);
                    }
                });
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public static SettingsFragment getInstance() {
            return new SettingsFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {

            Category view = new ThemedCategory();
            view.titleRes = R.string.tile_category_view;

            TransitionAnimationTile animationTile = new TransitionAnimationTile(getContext());
            view.addTile(animationTile);

            Category strategy = new ThemedCategory();
            strategy.titleRes = R.string.tile_category_strategy;

            InstallDataTile installDataTile = new InstallDataTile(getContext());
            DevTile devTile = new DevTile(getContext());
            strategy.addTile(installDataTile);
            strategy.addTile(new AutoInstallTile(getContext()));
            strategy.addTile(devTile);

            Category secure = new ThemedCategory();
            secure.titleRes = R.string.title_secure;
            secure.addTile(new EncryptTile(getContext()));

            Category storage = new ThemedCategory();
            storage.titleRes = R.string.tile_category_storage;

            storage.addTile(new StorageLocationTile(getContext()));

            if (SettingsProvider.isDebugEnabled()) {
                categories.add(view);
            }
            categories.add(strategy);
            categories.add(storage);
            categories.add(secure);

            super.onCreateDashCategories(categories);
        }
    }
}
