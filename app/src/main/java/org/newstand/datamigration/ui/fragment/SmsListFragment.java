package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.data.SMSRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SmsListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Sms;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher));
                super.onBindViewHolder(holder, record);
                SMSRecord smsRecord = (SMSRecord) record;
                holder.getLineOneTextView().setText(smsRecord.getMsg());
                holder.getLineTwoTextView().setText(smsRecord.getAddr() + "@" + smsRecord.getTime());
            }
        };
    }
}
