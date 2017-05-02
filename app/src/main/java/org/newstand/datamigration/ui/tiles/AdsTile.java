package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.AdActivity;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.utils.EmojiUtils;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AdsTile extends ThemedTile {

    public AdsTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(final Context context) {

        this.title = context.getString(R.string.title_show_ads, EmojiUtils.getEmojiByUnicode(0x1F602));
        this.iconRes = R.drawable.ic_ad;
        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                transitionSafeActivity.transitionTo(new Intent(context, AdActivity.class));
            }
        };
    }
}
