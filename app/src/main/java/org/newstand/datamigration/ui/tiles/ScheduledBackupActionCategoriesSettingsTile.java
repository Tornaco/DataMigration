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

    public ScheduledBackupActionCategoriesSettingsTile(@NonNull final Context context, final BackupActionSettings settings) {
        super(context, settings);

        this.titleRes = R.string.title_settings_categories;
        this.summary = buildSummary(settings.getDataCategories());
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
                                    if (checked && !settings.getDataCategories().contains(DataCategory.Contact))
                                        settings.getDataCategories().add(DataCategory.Contact);
                                    else
                                        settings.getDataCategories().remove(DataCategory.Contact);
                                    break;
                                case R.id.action_calllog:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.CallLog))
                                        settings.getDataCategories().add(DataCategory.CallLog);
                                    else
                                        settings.getDataCategories().remove(DataCategory.CallLog);
                                    break;
                                case R.id.action_sms:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.Sms))
                                        settings.getDataCategories().add(DataCategory.Sms);
                                    else
                                        settings.getDataCategories().remove(DataCategory.Sms);
                                    break;
                                case R.id.action_photo:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.Photo))
                                        settings.getDataCategories().add(DataCategory.Photo);
                                    else
                                        settings.getDataCategories().remove(DataCategory.Photo);
                                    break;
                                case R.id.action_music:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.Music))
                                        settings.getDataCategories().add(DataCategory.Music);
                                    else
                                        settings.getDataCategories().remove(DataCategory.Music);
                                    break;
                                case R.id.action_app:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.App))
                                        settings.getDataCategories().add(DataCategory.App);
                                    else
                                        settings.getDataCategories().remove(DataCategory.App);
                                    break;
                                case R.id.action_video:
                                    if (checked && !settings.getDataCategories().contains(DataCategory.Video))
                                        settings.getDataCategories().add(DataCategory.Video);
                                    else
                                        settings.getDataCategories().remove(DataCategory.Video);
                                    break;
                            }

                            getSummaryTextView().setText(buildSummary(settings.getDataCategories()));

                            return false;
                        }
                    });

                }

                popupMenu.show();
            }
        };
    }

    private String buildSummary(List<DataCategory> categories) {
        if(Collections.isNullOrEmpty(categories)) {
            return getContext().getString(R.string.summary_settings_categories);
        }
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
