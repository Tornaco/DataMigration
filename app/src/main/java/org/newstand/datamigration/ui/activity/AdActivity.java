package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.tbruyelle.rxpermissions2.RxPermissions;

import net.youmi.android.AdManager;
import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.AdsManager;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.EmojiUtils;
import org.newstand.logger.Logger;

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
        setContentView(R.layout.layout_ad_container);

        requestPerms();

        AdManager.getInstance(this).init(AdsManager.APP_ID, AdsManager.APP_SECRET, SettingsProvider.isDebugEnabled());

        final LinearLayout container = findView(R.id.ad_container);

        Runnable loader = new Runnable() {
            @Override
            public void run() {
                View banner = BannerManager.getInstance(getApplicationContext())
                        .getBannerView(getApplicationContext(),
                                new BannerViewListener() {
                                    @Override
                                    public void onRequestSuccess() {
                                        Logger.v("BannerManager, onRequestSuccess");
                                    }

                                    @Override
                                    public void onSwitchBanner() {
                                        Logger.v("BannerManager, onSwitchBanner");
                                    }

                                    @Override
                                    public void onRequestFailed() {
                                        Logger.v("BannerManager, onRequestFailed");
                                    }
                                });

                if (banner != null) {
                    container.addView(banner);
                }
            }
        };

        SharedExecutor.runOnUIThread(loader);
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
}
