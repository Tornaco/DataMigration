package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.R;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class RulesIntroTile extends QuickTile {

    public RulesIntroTile(@NonNull Context context) {
        super(context, null);

        this.titleRes = R.string.title_rule_intro;
        this.summaryRes = R.string.summary_rule_intro;
        this.iconRes = R.drawable.ic_help;

        this.tileView = new QuickTileView(context, this);
    }

}
