package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.activity.WFDDataReceiverActivity;
import org.newstand.datamigration.ui.widget.StaticColorQuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ReceiveTile extends ThemedTile {

    public ReceiveTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_transport_receiver;
        this.iconRes = R.drawable.ic_download;
        this.tileView = new StaticColorQuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                transitionSafeActivity.transitionTo(new Intent(getContext(), WFDDataReceiverActivity.class));
            }
        };
    }
}
