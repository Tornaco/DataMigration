package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Nick@NewStand.org on 2017/3/14 16:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AlertDialogCompat {

    public static AlertDialog create(Context context, String errTitle, String errMessage) {
        return new AlertDialog.Builder(context)
                .setTitle(errTitle)
                .setMessage(errMessage)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Empty.
                    }
                }).create();
    }

    public static AlertDialog createShow(Context context, String errTitle, String errMessage) {
        AlertDialog dialog = create(context, errTitle, errMessage);
        dialog.show();
        return dialog;
    }

    public static void dismiss(AlertDialog alertDialog) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
