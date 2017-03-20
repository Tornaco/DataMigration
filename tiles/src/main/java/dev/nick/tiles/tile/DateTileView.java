package dev.nick.tiles.tile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

public class DateTileView extends TileView {

    DatePickerDialog mDatePickerDialog;

    public DateTileView(Context context) {
        super(context);
    }

    public DateTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                getSummaryTextView().setText(String.valueOf(year + "-" + monthOfYear + "-" + dayOfMonth));
            }
        }, 1991, 9, 12);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        mDatePickerDialog.show();
    }
}
