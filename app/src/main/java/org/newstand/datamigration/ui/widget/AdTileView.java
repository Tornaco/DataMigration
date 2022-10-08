package org.newstand.datamigration.ui.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;

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
