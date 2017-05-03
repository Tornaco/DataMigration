package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.utils.EmojiUtils;

import java.util.Observable;
import java.util.Observer;

import dev.nick.eventbus.EventBus;
import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class StorageLocationTile extends ThemedTile {

    public StorageLocationTile(@NonNull Context context) {
        super(context, null);
    }

    private Observer oneTimeObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            TransitionSafeActivity safeActivity = (TransitionSafeActivity) getContext();
            if (!safeActivity.isDestroyedCompat()) {
                summary = getContext().getString(R.string.summary_storage_location,
                        SettingsProvider.getDataMigrationRootDir(), EmojiUtils.getEmojiByUnicode(0x1F6A9), EmojiUtils.getEmojiByUnicode(0x1F6A9));
                getTileView().getSummaryTextView().setText(summary);
            }
            SettingsProvider.unObserve(this);
        }
    };

    @Override
    void onInitView(final Context context) {

        this.titleRes = R.string.title_storage_location;
        this.summary = getContext().getString(R.string.summary_storage_location,
                SettingsProvider.getDataMigrationRootDir(), EmojiUtils.getEmojiByUnicode(0x1F6A9), EmojiUtils.getEmojiByUnicode(0x1F6A9));
        this.iconRes = R.drawable.ic_sd;

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);

                SettingsProvider.observe(oneTimeObserver);

                EventBus.from(context).publishEmptyEvent(IntentEvents.EVENT_FILE_PICKER);
            }
        };
    }
}
