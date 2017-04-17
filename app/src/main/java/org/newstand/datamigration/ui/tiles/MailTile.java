package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class MailTile extends ThemedTile {

    public MailTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_feedback;
        this.summary = getContext().getString(R.string.summary_feedback, "tornaco@163.com");// FIXME Move to settings.
        this.iconRes = R.drawable.ic_feed_back;

        this.tileView = new QuickTileView(getContext(), this);
    }

}
