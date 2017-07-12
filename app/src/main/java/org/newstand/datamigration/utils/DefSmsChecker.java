package org.newstand.datamigration.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.logger.Logger;

public class DefSmsChecker {

    public static void checkSmsAppSettings(Context context) {
        boolean weAreDef = SmsContentProviderCompat.areWeDefSmsApp(context);
        Logger.w("We are default Sms app, this is FATAL error");

        if (weAreDef) {
            new MaterialDialog.Builder(context)
                    .titleColorAttr(R.attr.colorAccent)
                    .title(R.string.warn_def_sms_app_title)
                    .content(R.string.warn_def_sms_app_message)
                    .cancelable(false)
                    .positiveColorAttr(R.attr.colorAccent)
                    .positiveText(android.R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {
                            Intent intent = new Intent();
                            // FIXME Launch app settings.
                        }
                    })
                    .build().show();

        }
    }
}
