package org.newstand.datamigration.ui.fragment;

import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.WifiRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class WifiListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Wifi;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_wifi_avatar));
                String psk = ((WifiRecord) record).getPsk();
                if (TextUtils.isEmpty(psk)) {
                    psk = "*";
                }
                holder.getLineTwoTextView().setText(psk);
                super.onBindViewHolder(holder, record);
            }
        };
    }


}
