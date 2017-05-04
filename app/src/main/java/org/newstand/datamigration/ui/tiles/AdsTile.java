package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.widget.AdTileView;

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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            this.title = context.getString(R.string.title_show_ads, EmojiUtils.getEmojiByUnicode(0x1F602));
//        } else {
//            this.title = context.getString(R.string.title_show_ads, ":0");
//        }
        this.iconRes = R.drawable.ic_ad;
//        this.tileView = new StaticColorQuickTileView(getContext(), this) {
//            @Override
//            public void onClick(View v) {
//                super.onClick(v);
//                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
//                transitionSafeActivity.transitionTo(new Intent(context, AdActivity.class));
//            }
//        };

        this.tileView = new AdTileView(getContext());
    }
}
