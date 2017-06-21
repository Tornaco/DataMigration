package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.SettingsRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/31 10:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SystemSettingsListFragment extends DataListViewerFragment {

    @Override
    protected void onLoadFinish(Collection<DataRecord> collection) {
        super.onLoadFinish(collection);
    }

    @Override
    DataCategory getDataType() {
        return DataCategory.SystemSettings;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                SettingsRecord settingsRecord = (SettingsRecord) record;
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_ext_avatar));
                holder.getLineTwoTextView().setText(settingsRecord.getValue());
                super.onBindViewHolder(holder, settingsRecord);
            }
        };
    }
}
