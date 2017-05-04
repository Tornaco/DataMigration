package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wandoujia.ads.sdk.Ads;

import org.newstand.datamigration.R;
import org.newstand.datamigration.secure.AdsManager;
import org.newstand.datamigration.strategy.Interval;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.sync.Sleeper;
import org.newstand.logger.Logger;

import dev.nick.tiles.tile.TileView;

/**
 * Created by Nick@NewStand.org on 2017/5/4 13:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AdTileView extends TileView {

    private ImageView mImageView;

    public AdTileView(Context context) {
        super(context);
    }

    public AdTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_ad_container;
    }

    @Override
    protected void onViewInflated(final View view) {

        mImageView = (ImageView) view.findViewById(R.id.icon);
        mImageView.setColorFilter(ContextCompat.getColor(getContext(), dev.nick.tiles.R.color.tile_icon_tint));

        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Ads.init(getContext(), AdsManager.Ali.appId(), AdsManager.Ali.appSecret());
                    Ads.preLoad(AdsManager.Ali.banner(), Ads.AdFormat.banner);
                } catch (Exception e) {
                    Logger.e("Fail init ad %s", Logger.getStackTraceString(e));
                    return;
                }

                Sleeper.sleepQuietly(Interval.Seconds.getIntervalMills() * 3);

                SharedExecutor.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ViewGroup container = (ViewGroup) view.findViewById(R.id.ad_container);
                            View bannerView = Ads.createBannerView(getContext(),
                                    AdsManager.Ali.banner());
                            Logger.d("Ad view loaded %s", bannerView);
                            container.addView(bannerView, generateCenterParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                        } catch (Throwable e) {
                            Logger.e("Fail load ad view", Logger.getStackTraceString(e));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected RelativeLayout.LayoutParams generateCenterParams(int w, int h) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.icon);
        return params;
    }

    @Override
    public ImageView getImageView() {
        return mImageView;
    }

    @Override
    protected void onBindActionView(RelativeLayout container) {

    }

    @Override
    public void setDividerVisibility(boolean visible) {
    }
}
