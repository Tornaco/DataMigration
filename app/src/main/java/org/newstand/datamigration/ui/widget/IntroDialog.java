package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/4/7 16:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class IntroDialog {

    public static void attach(Context context, final DialogInterface.OnCancelListener listener,
                              final Runnable onOkRunnable) {
        if (SettingsProvider.isUserNoticed()) {
            onOkRunnable.run();
            return;
        }
//        AlertDialog alertDialog = new AlertDialog.Builder(context)
//                .setTitle(R.string.title_user_notice)
//                .setMessage(R.string.message_user_notice)
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        listener.onCancel(dialog);
//                    }
//                })
//                .setNeutralButton(R.string.title_never_remind,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                SettingsProvider.setUserNoticed(true);
//                                onOkRunnable.run();
//                            }
//                        })
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        onOkRunnable.run();
//                    }
//                })
//                .setCancelable(false)
//                .create();
//        alertDialog.show();

        new MaterialDialog.Builder(context)
                .titleColorAttr(R.attr.colorPrimary)
                .title(R.string.title_user_notice)
                .content(R.string.message_user_notice)
                .negativeColorAttr(R.attr.colorPrimary)
                .positiveColorAttr(R.attr.colorPrimary)
                .neutralColorAttr(R.attr.colorPrimary)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        listener.onCancel(dialog);
                    }
                })
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        onOkRunnable.run();
                    }
                })
                .neutralText(R.string.title_never_remind)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SettingsProvider.setUserNoticed(true);
                        onOkRunnable.run();
                    }
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .build()
                .show();
    }
}
