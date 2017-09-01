package org.newstand.datamigration.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;

import org.newstand.datamigration.R;
import org.newstand.datamigration.policy.MinAdPresentTimesPolicy;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.tiles.MailTile;
import org.newstand.datamigration.ui.tiles.RaterTile;
import org.newstand.datamigration.ui.tiles.ShowAdTile;
import org.newstand.datamigration.ui.tiles.ThanksTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/14 17:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ComeInActivity extends TransitionSafeActivity {
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

            Category involve = new ThemedCategory();
            involve.titleRes = R.string.tile_category_in;

            involve.addTile(new MailTile(getActivity()));
            involve.addTile(new ThanksTile(getActivity()));
            involve.addTile(new RaterTile(getContext()));


            if (SettingsProvider.getAdPresentTimes() >= MinAdPresentTimesPolicy.getMinAdPresentTimes()) {
                involve.addTile(new ShowAdTile(getContext()));
            }

            categories.add(involve);

            super.onCreateDashCategories(categories);
        }
    }
}
