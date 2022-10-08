package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.activity.WFDDataSenderActivity;
import org.newstand.datamigration.ui.widget.StaticColorQuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SendTile extends ThemedTile {

    public SendTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_transport_sender;
        this.iconRes = R.drawable.ic_send;
        this.tileView = new StaticColorQuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                transitionSafeActivity.transitionTo(new Intent(getContext(), WFDDataSenderActivity.class));
            }
        };
    }
}
