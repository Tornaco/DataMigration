package org.newstand.datamigration.ui.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import dev.nick.tiles.tile.TileView;

public class TimeTileView extends TileView {

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private OnDateSetListener listener;

    public TimeTileView(Context context, long time) {
        super(context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mTimePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        mTimePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                listener.onDateSet(mDatePickerDialog.getDatePicker(),
                        mDatePickerDialog.getDatePicker().getYear(),
                        mDatePickerDialog.getDatePicker().getMonth(),
                        mDatePickerDialog.getDatePicker().getDayOfMonth(),
                        hourOfDay, minute);
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
    }

    public void setListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        mDatePickerDialog.show();
    }

    public interface OnDateSetListener {
        /**
         * @param view       the picker associated with the dialog
         * @param year       the selected year
         * @param month      the selected month (0-11 for compatibility with
         *                   {@link Calendar#MONTH})
         * @param dayOfMonth th selected day of the month (1-31, depending on
         *                   month)
         */
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth, int hourOfDay, int minute);
    }
}
