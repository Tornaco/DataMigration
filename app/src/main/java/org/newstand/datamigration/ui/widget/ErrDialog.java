package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import org.newstand.datamigration.R;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/4/14 13:31
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ErrDialog {

    public static void attach(Context context, Throwable throwable, @Nullable final DialogInterface.OnDismissListener listener) {
        attach(context, Logger.getStackTraceString(throwable), listener);
    }

    public static void attach(Context context, String errMsg,
                              @Nullable final DialogInterface.OnDismissListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.ErrDialog)
                .setTitle(R.string.title_err)
                .setMessage(errMsg)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener(listener)
                .setCancelable(false)
                .create();
        alertDialog.show();
    }
}
