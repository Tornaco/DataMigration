package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.service.schedule.ActionSettings;
import org.newstand.datamigration.service.schedule.BackupActionSettings;
import org.newstand.datamigration.utils.Collections;
import org.newstand.logger.Logger;

import java.util.List;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/21 22:33
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledBackupActionCategoriesSettingsTile extends ScheduledBackupActionSettingsTile {

    private PopupMenu popupMenu;

    public ScheduledBackupActionCategoriesSettingsTile(@NonNull final Context context, final ActionSettings settings) {
        super(context, settings);

        final BackupActionSettings backupActionSettings = (BackupActionSettings) settings;

        this.titleRes = R.string.title_settings_categories;
        this.summary = buildSummary(((BackupActionSettings) settings).getDataCategories());
        this.iconRes = R.drawable.ic_backup;


        this.tileView = new QuickTileView(context, this) {

            @Override
            protected void onBindActionView(RelativeLayout container) {
                super.onBindActionView(container);
            }

            @Override
            public void onClick(View v) {
                super.onClick(v);

                if (popupMenu == null) {
                    popupMenu = new PopupMenu(context, this);
                    popupMenu.inflate(R.menu.category_selector);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            item.setChecked(!item.isChecked());
                            boolean checked = item.isChecked();
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.action_contact:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.Contact))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.Contact);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.Contact);
                                    break;
                                case R.id.action_calllog:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.CallLog))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.CallLog);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.CallLog);
                                    break;
                                case R.id.action_sms:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.Sms))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.Sms);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.Sms);
                                    break;
                                case R.id.action_photo:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.Photo))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.Photo);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.Photo);
                                    break;
                                case R.id.action_music:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.Music))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.Music);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.Music);
                                    break;
                                case R.id.action_app:
                                    if (checked && !((BackupActionSettings) settings).getDataCategories().contains(DataCategory.App))
                                        ((BackupActionSettings) settings).getDataCategories().add(DataCategory.App);
                                    else
                                        ((BackupActionSettings) settings).getDataCategories().remove(DataCategory.App);
                                    break;
                            }

                            getSummaryTextView().setText(buildSummary(((BackupActionSettings) settings).getDataCategories()));

                            return false;
                        }
                    });

                }

                popupMenu.show();
            }
        };
    }

    private String buildSummary(List<DataCategory> categories) {
        final StringBuilder sb = new StringBuilder();
        Collections.consumeRemaining(categories, new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory dataCategory) {
                sb.append(getContext().getString(dataCategory.nameRes()));
                sb.append("\t");
            }
        });
        Logger.d("build summary %s", sb.toString());
        return sb.toString();
    }

}
