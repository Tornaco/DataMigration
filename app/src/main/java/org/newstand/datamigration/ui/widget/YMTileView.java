package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;

import org.newstand.datamigration.R;
import org.newstand.logger.Logger;

import dev.nick.tiles.tile.TileView;

/**
 * Created by Nick@NewStand.org on 2017/5/2 16:56
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class YMTileView extends TileView {

    public YMTileView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_ad_container;
    }

    @Override
    protected void onViewInflated(View view) {
        Logger.i("Loading ads...");
        View banner = BannerManager.getInstance(getContext()).getBannerView(getContext(),
                new BannerViewListener() {
            @Override
            public void onRequestSuccess() {
                Logger.d("BannerManager, onRequestSuccess");
            }

            @Override
            public void onSwitchBanner() {
                Logger.d("BannerManager, onSwitchBanner");
            }

            @Override
            public void onRequestFailed() {
                Logger.d("BannerManager, onRequestFailed");
            }
        });

        if (banner != null) {
            LinearLayout container = (LinearLayout) view.findViewById(R.id.ad_container);
            container.addView(banner);
        }
    }

    @Override
    protected void onBindActionView(RelativeLayout container) {

    }

    @Override
    public void setDividerVisibility(boolean visible) {
    }
}
