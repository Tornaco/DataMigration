package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.AppRecord;

/**
 * Created by Nick@NewStand.org on 2017/5/2 10:23
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ApkDataPickerDialog {

    public static void attach(Context context, final AppRecord appRecord, DialogInterface.OnDismissListener onDismissListener) {
        new AlertDialog.Builder(context)
                .setMultiChoiceItems(R.array.apk_data_selections, new boolean[]{appRecord.isHandleApk(),
                                appRecord.isHandleData()},
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which,
                                                boolean isChecked) {
                                if (which == 0) { // Apk index is 0.
                                    appRecord.setHandleApk(isChecked);
                                } else {
                                    appRecord.setHandleData(isChecked);
                                }
                            }
                        })
                .setOnDismissListener(onDismissListener)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
