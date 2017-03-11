package org.newstand.datamigration.ui.widget;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:28
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ProgressDialogCompat {

    public static ProgressDialog createUnCancelableIndeterminate(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
}
