package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.policy.ExtraDataRule;
import org.newstand.datamigration.ui.widget.PackagePickerDialog;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/28 13:16
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class PackageNameTile extends QuickTile {

    public PackageNameTile(@NonNull final Context context, final ExtraDataRule rule) {
        super(context, null);

        this.titleRes = R.string.title_package_name;

        String alias = rule.getAlias();
        if (TextUtils.isEmpty(alias)) {
            this.summaryRes = R.string.title_not_set;
        } else {
            this.summary = alias;
        }

        this.iconRes = R.drawable.ic_child;

        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                PackagePickerDialog.attach(context, rule.getPackageName(), new Consumer<AppRecord>() {
                    @Override
                    public void accept(@NonNull AppRecord appRecord) {
                        rule.setAlias(appRecord.getDisplayName());
                        rule.setPackageName(appRecord.getPkgName());

                        getTileView().getSummaryTextView().setText(rule.getAlias());
                    }
                });
            }
        };
    }
}
