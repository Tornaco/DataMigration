package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.widget.ApkDataPickerDialog;
import org.newstand.datamigration.utils.Files;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AppListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.App;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.data_list_app, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                AppRecord appRecord = (AppRecord) record;
                String summary = getString(R.string.summary_app_template, appRecord.getVersionName()
                                == null ? getString(R.string.unknown) : appRecord.getVersionName(),
                        Files.formatSize(appRecord.getSize()),
                        getStringSafety(appRecord.isHandleApk() ? R.string.yes : R.string.no),
                        getStringSafety(appRecord.isHandleData() ? R.string.yes : R.string.no));
                holder.getLineTwoTextView().setText(summary);
                Vangogh.with(AppListFragment.this)
                        .load(appRecord.getIconUrl())
                        .fallback(R.mipmap.ic_ext_avatar)
                        .applier(new FadeOutFadeInApplier())
                        .into(holder.getCheckableImageView());
                super.onBindViewHolder(holder, record);
            }

            @Override
            protected void onItemClick(final CommonListViewHolder holder) {
                final AppRecord r = (AppRecord) getDataRecords().get(holder.getAdapterPosition());
                ApkDataPickerDialog.attach(getActivity(), r,
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                boolean willCheck = r.isHandleApk() || r.isHandleData();
                                onCheckStateChanged(willCheck, holder.getAdapterPosition());
                                onUpdate();
                            }
                        });
            }

            @Override
            public void onCheckableImageClick(CommonListViewHolder holder) {
                final AppRecord appRecord = (AppRecord) getDataRecords().get(holder.getAdapterPosition());
                appRecord.setHandleApk(true);
                appRecord.setHandleData(true);
                onCheckStateChanged(true, holder.getAdapterPosition());
                onUpdate();
            }

            @Override
            public void selectAll(boolean select) {
                synchronized (dataRecords) {
                    for (DataRecord c : dataRecords) {
                        c.setChecked(select);
                        AppRecord ar = (AppRecord) c;
                        ar.setHandleApk(select);
                        ar.setHandleData(select);
                    }
                }
                notifyDataSetChanged();
            }
        };
    }


}
