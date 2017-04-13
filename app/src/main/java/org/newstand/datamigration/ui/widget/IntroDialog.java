package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/4/7 16:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class IntroDialog {

    public static void attach(Context context, final DialogInterface.OnCancelListener listener) {
        if (!SettingsProvider.isDebugEnabled() && SettingsProvider.isUserNoticed()) return;
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_user_notice)
                .setMessage(R.string.message_user_notice)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel(dialog);
                    }
                })
                .setNeutralButton(R.string.title_never_remind,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsProvider.setUserNoticed(true);
                            }
                        })
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create();
        alertDialog.show();
    }
}
