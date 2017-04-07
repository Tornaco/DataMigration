package org.newstand.datamigration.ui.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.AppRecord;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;

/**
 * Created by Nick@NewStand.org on 2017/4/7 15:26
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupAppListFragment extends AppListFragment {
    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_app_avatar));
                super.onBindViewHolder(holder, record);
                AppRecord appRecord = (AppRecord) record;
                String summary = getStringSafety(R.string.summary_app_template2, appRecord.getVersionName()
                                == null ? getString(R.string.unknown) : appRecord.getVersionName(),
                        Files.formatSize(appRecord.getSize()), getStringSafety(appRecord.isHasData() ? R.string.yes : R.string.no));
                holder.getLineTwoTextView().setText(summary);
                Drawable icon = appRecord.getIcon();
                holder.getCheckableImageView().setImageDrawable(icon);
            }
        };
    }
}
