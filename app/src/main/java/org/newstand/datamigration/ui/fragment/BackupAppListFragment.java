package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.widget.ApkDataPickerDialog;
import org.newstand.datamigration.utils.Files;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;

/**
 * Created by Nick@NewStand.org on 2017/4/7 15:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupAppListFragment extends AppListFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                AppRecord appRecord = (AppRecord) record;
                String summary = getStringSafety(R.string.summary_app_template2, appRecord.getVersionName()
                                == null ? getString(R.string.unknown) : appRecord.getVersionName(),
                        Files.formatSize(appRecord.getSize()),
                        getStringSafety(appRecord.isHasApk() ? R.string.yes : R.string.no),
                        getStringSafety(appRecord.isHasData() ? R.string.yes : R.string.no),
                        getStringSafety(appRecord.isHasExtraData() ? R.string.yes : R.string.no),
                        getStringSafety(appRecord.isHandleApk() ? R.string.yes : R.string.no),
                        getStringSafety(appRecord.isHandleData() ? R.string.yes : R.string.no));
                holder.getLineTwoTextView().setText(summary);
                Vangogh.with(BackupAppListFragment.this)
                        .load(appRecord.getIconUrl())
                        .applier(new FadeOutFadeInApplier())
                        .fallback(R.mipmap.ic_ext_avatar)
                        .into(holder.getCheckableImageView());
                super.onBindViewHolder(holder, record);
            }

            @Override
            protected void onItemClick(final CommonListViewHolder holder) {
                final AppRecord r = (AppRecord) getDataRecords().get(holder.getAdapterPosition());

                if (r.isHasApk() && r.isHasData()) {
                    ApkDataPickerDialog.attach(getActivity(), r,
                            new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    boolean willCheck = r.isHandleApk() || r.isHandleData();
                                    onCheckStateChanged(willCheck, holder.getAdapterPosition());
                                    onUpdate();
                                }
                            });
                } else {
                    if (r.isHasApk()) {
                        r.setHandleApk(!r.isHandleApk());
                    } else if (r.isHasData()) {
                        r.setHandleData(!r.isHandleData());
                    }
                    r.setChecked(r.isHandleApk() || r.isHandleData());
                    onUpdate();
                }
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
                        if (!select) {
                            ar.setHandleApk(false);
                            ar.setHandleData(false);
                        } else {
                            ar.setHandleApk(ar.isHasApk());
                            ar.setHandleData(ar.isHasData());
                        }
                    }
                }
                notifyDataSetChanged();
            }
        };
    }
}
