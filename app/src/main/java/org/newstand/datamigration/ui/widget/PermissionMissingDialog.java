package org.newstand.datamigration.ui.widget;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import org.newstand.datamigration.R;

public class PermissionMissingDialog {

    public static void attach(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.permission_missing)
//                .titleColorAttr(R.attr.colorAccent)
                .content(R.string.permission_missing_message)
                .cancelable(false)
                .positiveColorAttr(R.attr.colorAccent)
                .positiveText(android.R.string.ok)
                .build()
                .show();
    }
}
