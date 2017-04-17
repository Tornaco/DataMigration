package org.newstand.datamigration.ui.tiles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.activity.TransitionSafeActivity;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;

import dev.nick.tiles.tile.QuickTileView;

/**
 * Created by Nick@NewStand.org on 2017/4/6 18:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CheckForUpdateTile extends ThemedTile {

    public CheckForUpdateTile(@NonNull Context context) {
        super(context, null);
    }

    @Override
    void onInitView(Context context) {

        this.titleRes = R.string.title_check_for_update;
        this.iconRes = R.drawable.ic_update;
        this.summary = getContext().getString(R.string.summary_check_for_update_current_version,
                VersionRetriever.currentVersionName());

        this.tileView = new QuickTileView(getContext(), this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                checkForUpdate();
            }
        };
    }

    private void checkForUpdate() {
        VersionRetriever.hasLaterVersionAsync(getContext(), new ActionListener2Adapter<VersionCheckResult, Throwable>() {
            @Override
            public void onComplete(final VersionCheckResult versionCheckResult) {
                super.onComplete(versionCheckResult);
                SharedExecutor.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (versionCheckResult.isHasLater())
                            showUpdateSnake(versionCheckResult.getVersionInfo());
                        else {
                            TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                            if (!transitionSafeActivity.isDestroyedCompat()) {
                                Snackbar.make(getTileView(), R.string.title_new_already_latest, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(final Throwable throwable) {
                super.onError(throwable);
                SharedExecutor.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
                        if (transitionSafeActivity.isDestroyedCompat()) {
                            return;
                        }
                        Snackbar.make(getTileView(), R.string.title_update_check_fail, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action_look_up, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ErrDialog.attach(getContext(), throwable, null);
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }

    private void showUpdateSnake(final VersionInfo info) {
        TransitionSafeActivity transitionSafeActivity = (TransitionSafeActivity) getContext();
        if (transitionSafeActivity.isDestroyedCompat()) {
            return;
        }
        Snackbar.make(getTileView(),
                getContext().getString(R.string.title_new_update_available, info.getVersionName()),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_look_up, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestLookup(info);
                    }
                }).show();
    }

    private void onRequestLookup(VersionInfo info) {
        VersionInfoDialog.attach(getContext(), info);
    }
}
