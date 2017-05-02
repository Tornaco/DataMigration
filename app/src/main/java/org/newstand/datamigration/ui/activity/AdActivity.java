package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.youmi.android.AdManager;
import net.youmi.android.normal.banner.BannerManager;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.AdsManager;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.tiles.YMTile;
import org.newstand.datamigration.utils.EmojiUtils;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/5/2 16:08
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AdActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getString(R.string.title_show_ads, EmojiUtils.getEmojiByUnicode(0x1F60F)));
        setContentView(R.layout.activity_with_container_template);
        AdManager.getInstance(this).init(AdsManager.APP_ID, AdsManager.APP_SECRET, SettingsProvider.isDebugEnabled());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AdFragment.getInstance()).commit();
    }

    public static class AdFragment extends DashboardFragment {

        public static AdFragment getInstance() {
            return new AdFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {

            for (int i = 0; i < 10; i++) {
                Category view = new ThemedCategory();
                view.addTile(new YMTile(getContext()));
                categories.add(view);
            }

            super.onCreateDashCategories(categories);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            BannerManager.getInstance(getContext()).onDestroy();
        }
    }
}
