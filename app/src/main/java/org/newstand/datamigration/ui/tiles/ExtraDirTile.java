package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import androidx.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.policy.ExtraDataRule;

import dev.nick.tiles.tile.EditTextTileView;
import dev.nick.tiles.tile.QuickTile;

/**
 * Created by Nick@NewStand.org on 2017/4/28 13:16
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ExtraDirTile extends QuickTile {

    public ExtraDirTile(@NonNull final Context context, final ExtraDataRule rule) {
        super(context, null);

        this.titleRes = R.string.title_dir_name;

        this.summary = buildSummary(rule);
        if (this.summary == null) {
            this.summaryRes = R.string.title_not_set;
        }

        this.iconRes = R.drawable.ic_files;

        this.tileView = new EditTextTileView(context) {
            @Override
            protected CharSequence getDialogTitle() {
                return context.getString(titleRes);
            }

            @Override
            protected CharSequence getHint() {
                return summary == null ? context.getString(R.string.title_dir_divider) : summary;
            }

            @Override
            protected void onPositiveButtonClick() {
                super.onPositiveButtonClick();

                String dirs = getEditText().getText().toString();

                if (ExtraDataRule.validateDir(dirs)) rule.setExtraDataDirs(dirs);

                summary = buildSummary(rule);
                if (summary == null) {
                    summaryRes = R.string.title_not_set;
                }
                getTileView().getSummaryTextView().setText(summary);
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

    private String buildSummary(ExtraDataRule rule) {
        String[] dirs = rule.parseDir();
        if (dirs == null) return null;
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < dirs.length; i++) {
            sb.append(dirs[i]);
            if (i != dirs.length - 1) sb.append("\n");
        }

        return sb.toString();
    }
}
