package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.tiles.BugReportTile;
import org.newstand.datamigration.ui.tiles.CheckForUpdateTile;
import org.newstand.datamigration.ui.tiles.DevTile;
import org.newstand.datamigration.ui.tiles.DonateTile;
import org.newstand.datamigration.ui.tiles.LicenceTile;
import org.newstand.datamigration.ui.tiles.MailTile;
import org.newstand.datamigration.ui.tiles.StorageLocationTile;
import org.newstand.datamigration.ui.tiles.ThanksTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.tiles.TransitionAnimationTile;
import org.newstand.datamigration.ui.tiles.WorkModeTile;

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

            Category view = new ThemedCategory();
            view.titleRes = R.string.tile_category_view;

            TransitionAnimationTile animationTile = new TransitionAnimationTile(getContext());
            view.addTile(animationTile);

            Category strategy = new ThemedCategory();
            strategy.titleRes = R.string.tile_category_strategy;

            WorkModeTile workModeTile = new WorkModeTile(getContext());
            DevTile devTile = new DevTile(getContext());
            strategy.addTile(workModeTile);
            strategy.addTile(devTile);

            Category storage = new ThemedCategory();
            storage.titleRes = R.string.tile_category_storage;

            storage.addTile(new StorageLocationTile(getContext()));

            Category about = new ThemedCategory();
            about.titleRes = R.string.tile_category_about;

            CheckForUpdateTile checkForUpdateTile = new CheckForUpdateTile(getActivity());
            about.addTile(checkForUpdateTile);
            LicenceTile licenceTile = new LicenceTile(getActivity());
            about.addTile(licenceTile);

            Category involve = new ThemedCategory();
            involve.titleRes = R.string.tile_category_in;

            involve.addTile(new BugReportTile(getActivity()));
            involve.addTile(new MailTile(getActivity()));
            involve.addTile(new ThanksTile(getActivity()));
            DonateTile donateTile = new DonateTile(getContext());
            involve.addTile(donateTile);

            if (SettingsProvider.isDebugEnabled()) {
                categories.add(view);
            }
            categories.add(strategy);
            categories.add(storage);
            categories.add(about);
            categories.add(involve);

            super.onCreateDashCategories(categories);
        }
    }
}
