package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;

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
import io.reactivex.functions.Consumer;

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

        requestPerms();

        AdManager.getInstance(this).init(AdsManager.APP_ID, AdsManager.APP_SECRET, SettingsProvider.isDebugEnabled());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AdFragment.getInstance()).commit();
    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        // Ignored.
                    }
                });
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
