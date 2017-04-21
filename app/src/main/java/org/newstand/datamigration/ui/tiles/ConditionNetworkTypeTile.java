package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.service.schedule.NetworkType;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
import java.util.List;

import dev.nick.tiles.tile.DropDownTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 21:22
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ConditionNetworkTypeTile extends ConditionItemTile {

    public ConditionNetworkTypeTile(@NonNull final Context context, final Condition condition) {
        super(context, condition);

        this.titleRes = R.string.title_condition_network_type;
        this.iconRes = R.drawable.ic_network_check;


        final List<String> typeStr = new ArrayList<>(NetworkType.values().length);
        final List<NetworkType> allType = new ArrayList<>(NetworkType.values().length);

        Collections.consumeRemaining(NetworkType.values(), new Consumer<NetworkType>() {
            @Override
            public void accept(@NonNull NetworkType networkType) {
                typeStr.add(context.getString(networkType.nameRes()));
                allType.add(networkType);
            }
        });

        this.summaryRes = allType.get(condition.getNetworkType()).nameRes();

        this.tileView = new DropDownTileView(context) {
            @Override
            protected List<String> onCreateDropDownList() {
                return typeStr;
            }

            @Override
            protected void onItemSelected(int position) {
                super.onItemSelected(position);
                condition.setNetworkType(allType.get(position).ordinal());
                updateSummary(context.getString(allType.get(position).nameRes()));
            }

            @Override
            protected int getInitialSelection() {
                return condition.getNetworkType();
            }
        };
    }

    private void updateSummary(String summary) {
        this.summary = summary;
        getTileView().getSummaryTextView().setText(summary);
    }
}
