package org.newstand.datamigration.ui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.DataLoaderManager;
import org.newstand.datamigration.loader.LoaderListenerMainThreadAdapter;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.utils.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nick@NewStand.org on 2017/4/28 13:21
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class PackagePickerDialog {

    public static void attach(final Context context, final String selectedPackage,
                              final Consumer<AppRecord> selectionConsumer) {

        final ProgressDialog progressDialog = ProgressDialogCompat.createUnCancelableIndeterminate(context);

        DataLoaderManager.from(context).loadAsync(LoaderSource.builder()
                        .parent(LoaderSource.Parent.Android).build(), DataCategory.App,
                new LoaderListenerMainThreadAdapter<DataRecord>() {
                    @Override
                    public void onStartMainThread() {
                        super.onStartMainThread();
                        progressDialog.show();
                    }

                    @Override
                    public void onCompleteMainThread(Collection<DataRecord> collection) {
                        super.onCompleteMainThread(collection);

                        ProgressDialogCompat.dismiss(progressDialog);

                        if (Collections.isNullOrEmpty(collection)) return;

                        final List<DataRecord> records = new ArrayList<>(collection.size());
                        records.addAll(collection);

                        int selectedIndex = -1;

                        CharSequence[] sequences = new CharSequence[records.size()];

                        for (int i = 0; i < sequences.length; i++) {
                            AppRecord r = (AppRecord) records.get(i);
                            sequences[i] = r.getDisplayName();
                            if (selectedPackage != null && selectedPackage.equals(r.getPkgName())) {
                                selectedIndex = i;
                            }
                        }

                        new AlertDialog.Builder(context)
                                .setTitle(R.string.title_package_name)
                                .setSingleChoiceItems(sequences, selectedIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectionConsumer.accept((AppRecord) records.get(which));
                                    }
                                })
                                .setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }

                    @Override
                    public void onErrMainThread(Throwable throwable) {
                        super.onErrMainThread(throwable);
                        ProgressDialogCompat.dismiss(progressDialog);
                    }
                });
    }
}
