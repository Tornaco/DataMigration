package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import org.newstand.datamigration.R;
import org.newstand.datamigration.service.schedule.Condition;
import org.newstand.datamigration.utils.DateUtils;

import java.util.Calendar;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.TimeTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ConditionTimeTile extends QuickTile {

    public ConditionTimeTile(@NonNull final Context context, final Condition condition) {
        super(context, null);

        this.titleRes = R.string.title_settings_time;
        this.iconRes = R.drawable.ic_clock;
        this.summaryRes = R.string.summary_settings_time;

        TimeTileView view = new TimeTileView(getContext(), condition.getTriggerAtMills());
        view.setListener(new TimeTileView.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                Long timeInMills = calendar.getTimeInMillis();
                condition.setTriggerAtMills(timeInMills);

                updateSummary(calendar);
            }
        });

        this.tileView = view;
    }

    private void updateSummary(Calendar calendar) {
        getTileView().getSummaryTextView().setText(DateUtils.formatLong(calendar.getTimeInMillis()));
    }
}
