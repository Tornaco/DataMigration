package org.newstand.datamigration.ui.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OnlyApkAppListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.App;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.data_list_app, menu);
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                AppRecord appRecord = (AppRecord) record;
                String summary = getString(R.string.summary_app_template_short, appRecord.getVersionName()
                                == null ? getString(R.string.unknown) : appRecord.getVersionName(),
                        Files.formatSize(appRecord.getSize()));
                holder.getLineTwoTextView().setText(summary);
                Drawable icon = appRecord.getIcon();
                holder.getCheckableImageView().setImageDrawable(icon == null
                        ? ContextCompat.getDrawable(getContext(), R.mipmap.ic_ext_avatar)
                        : icon);
                super.onBindViewHolder(holder, record);
            }

            @Override
            protected void onCheckStateChanged(boolean checked, int position) {
                super.onCheckStateChanged(checked, position);
                AppRecord r = (AppRecord) getDataRecords().get(position);
                r.setHandleApk(checked);
            }
        };
    }


}
