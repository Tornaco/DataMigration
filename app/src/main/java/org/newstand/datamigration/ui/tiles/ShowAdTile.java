package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.EmojiUtils;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.SwitchTileView;

/**
 * Created by Nick@NewStand.org on 2017/5/4 14:29
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ShowAdTile extends QuickTile {

    public ShowAdTile(@NonNull Context context) {
        super(context, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.title = context.getString(R.string.title_show_ads, EmojiUtils.getEmojiByUnicode(0x1F602));
        } else {
            this.title = context.getString(R.string.title_show_ads, ":0");
        }
        this.iconRes = R.drawable.ic_ad;

        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
                setChecked(SettingsProvider.isShowAdEnabled());
            }

            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
                SettingsProvider.setShowAdEnabled(checked);
            }
        };
    }
}
