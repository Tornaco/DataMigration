package org.newstand.datamigration.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.utils.DateUtils;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

/**
 * Created by Nick@NewStand.org on 2017/4/14 17:00
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VersionInfoDialog {

    public static void attach(Context context, VersionInfo info) {

        SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild();
        simplifySpanBuild
                .append(new SpecialTextUnit("Version name: ")
                        .setTextColor(Color.BLUE)
                        .useTextBold())
                .append(info.getVersionName())
                .append("\n")

                .append(new SpecialTextUnit("Version code: ")
                        .setTextColor(Color.BLUE)
                        .useTextBold())
                .append(String.valueOf(info.getVersionCode()))
                .append("\n")

                .append(new SpecialTextUnit("Description: ")
                        .setTextColor(Color.BLUE)
                        .useTextBold())
                .append(info.getUpdateDescription())
                .append("\n")

                .append(new SpecialTextUnit("Update date: ")
                        .setTextColor(Color.BLUE)
                        .useTextBold())
                .append(DateUtils.formatLong(info.getUpdateDate()))
                .append("\n")

                .build();

        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.version_info_textview, null, false);
        TextView textView = (TextView) root.findViewById(android.R.id.text1);
        textView.setText(simplifySpanBuild.build());

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_new_update_info)
                .setView(root)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(R.string.title_never_remind,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsProvider.setUserNoticed(true);
                            }
                        })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }
}
