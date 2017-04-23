package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.service.schedule.BackupActionSettings;

import dev.nick.tiles.tile.EditTextTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledBackupActionSessionSettingsTile extends ScheduledBackupActionSettingsTile {

    public ScheduledBackupActionSessionSettingsTile(@NonNull final Context context, BackupActionSettings settings) {
        super(context, settings);

        final BackupActionSettings backupActionSettings = settings;

        this.titleRes = R.string.title_settings_session;
        this.summary = backupActionSettings.getSession().getName();
        this.iconRes = R.drawable.ic_edit;

        this.tileView = new EditTextTileView(context) {

            @Override
            protected CharSequence getDialogTitle() {
                return context.getString(titleRes);
            }

            @Override
            protected CharSequence getHint() {
                return backupActionSettings.getSession().getName();
            }

            @Override
            protected void onPositiveButtonClick() {
                super.onPositiveButtonClick();
                backupActionSettings.getSession().setName(getEditText().getText().toString());
                getSummaryTextView().setText(backupActionSettings.getSession().getName());
            }

            @Override
            protected CharSequence getPositiveButton() {
                return context.getString(android.R.string.ok);
            }

            @Override
            protected CharSequence getNegativeButton() {
                return context.getString(android.R.string.cancel);
            }
        };
    }
}
