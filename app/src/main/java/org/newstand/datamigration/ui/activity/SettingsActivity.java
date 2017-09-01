package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import com.nononsenseapps.filepicker.Utils;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListenerMainThreadAdapter;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.ui.tiles.AutoInstallTile;
import org.newstand.datamigration.ui.tiles.ChangeLauncherIconTile;
import org.newstand.datamigration.ui.tiles.DevTile;
import org.newstand.datamigration.ui.tiles.EncryptTile;
import org.newstand.datamigration.ui.tiles.InstallDataTile;
import org.newstand.datamigration.ui.tiles.InstallerTimeoutTile;
import org.newstand.datamigration.ui.tiles.StorageLocationTile;
import org.newstand.datamigration.ui.tiles.ThemeColorTile;
import org.newstand.datamigration.ui.tiles.ThemedCategory;
import org.newstand.datamigration.ui.tiles.TransitionAnimationTile;
import org.newstand.datamigration.ui.widget.ProgressDialogCompat;
import org.newstand.datamigration.utils.FilePickerUtils;
import org.newstand.datamigration.utils.Files;
import org.newstand.logger.Logger;

import java.io.File;
import java.util.List;

import dev.nick.eventbus.Event;
import dev.nick.eventbus.EventBus;
import dev.nick.eventbus.EventReceiver;
import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/14 17:49
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SettingsActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        EventBus.from(this).subscribe(pickerEventReceiver);
        setContentView(R.layout.activity_with_container_template);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SettingsFragment.getInstance()).commit();
    }

    private EventReceiver pickerEventReceiver = new EventReceiver() {
        @Override
        public void onReceive(@NonNull Event event) {
            FilePickerUtils.pickSingleDir(SettingsActivity.this, IntentEvents.REQUEST_CODE_FILE_PICKER);
        }

        @Override
        public int[] events() {
            return new int[]{IntentEvents.EVENT_FILE_PICKER};
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.d("onActivityResult %s %s %s", requestCode, resultCode, data);
        if (requestCode == IntentEvents.REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);
            File file = Utils.getFileForUri(files.get(0));
            // Do something with the result...
            Logger.d("Picked %s", file);

            onStorageDirPick(file);
        }
    }

    private void onStorageDirPick(File dir) {

        boolean isEmpty = Files.isEmptyDir(dir);

        if (!isEmpty) {
            warnWithNoneEmptyDir();
            SettingsProvider.setDataMigrationRootDir(dir.getPath());
        } else {
            moveDirTo(dir);
        }

    }

    private void moveDirTo(final File dir) {

        final ProgressDialog dialog = ProgressDialogCompat.createUnCancelableIndeterminateShow(this);

        File from = new File(SettingsProvider.getDataMigrationRootDir());
        Files.moveAsync(from, dir, new ActionListenerMainThreadAdapter<Boolean>(Looper.getMainLooper()) {
            @Override
            public void onActionMainThread(@Nullable Boolean res) {
                ProgressDialogCompat.dismiss(dialog);
                if (res != null && res) {
                    SettingsProvider.setDataMigrationRootDir(dir.getPath());
                }
                showMoveResult(res == null ? false : res);
            }
        });
    }

    private void warnWithNoneEmptyDir() {
        Snackbar.make(getContentView(), R.string.warn_storage_location_empty, Snackbar.LENGTH_LONG).show();
    }

    private void showMoveResult(boolean res) {
        Snackbar.make(getContentView(), res ? R.string.res_storage_location_move_ok : R.string.res_storage_location_move_fail, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.from(this).unSubscribe(pickerEventReceiver);
    }

    public static class SettingsFragment extends DashboardFragment {

        public static SettingsFragment getInstance() {
            return new SettingsFragment();
        }

        @Override
        protected void onCreateDashCategories(List<Category> categories) {

            Category view = new ThemedCategory();
            view.titleRes = R.string.tile_category_view;

            TransitionAnimationTile animationTile = new TransitionAnimationTile(getContext());
            view.addTile(animationTile);

            view.addTile(new ThemeColorTile(getActivity()));
            view.addTile(new ChangeLauncherIconTile(getActivity()));

            Category dev = new ThemedCategory();
            dev.titleRes = R.string.tile_category_dev;

            DevTile devTile = new DevTile(getContext());
            dev.addTile(devTile);

            Category app = new ThemedCategory();
            app.titleRes = R.string.tile_category_app;
            app.addTile(new InstallDataTile(getContext()));
            app.addTile(new AutoInstallTile(getContext()));
            app.addTile(new InstallerTimeoutTile(getContext()));

            Category secure = new ThemedCategory();
            secure.titleRes = R.string.title_secure;
            secure.addTile(new EncryptTile(getContext()));

            Category storage = new ThemedCategory();
            storage.titleRes = R.string.tile_category_storage;

            storage.addTile(new StorageLocationTile(getActivity()));

            categories.add(view);
            categories.add(secure);
            categories.add(app);
            categories.add(storage);
            categories.add(dev);

            super.onCreateDashCategories(categories);
        }
    }
}
