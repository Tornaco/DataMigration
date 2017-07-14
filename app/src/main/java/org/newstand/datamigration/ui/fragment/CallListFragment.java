package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.CallLogCompat;
import org.newstand.datamigration.data.model.CallLogRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.logger.Logger;

import java.util.Date;

import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CallListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.CallLog;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                CallLogRecord callLogRecord = (CallLogRecord) record;
                int type = callLogRecord.getType();
                if (type == CallLogCompat.OUTGOING_TYPE) {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_call_avatar_made));
                } else if (type == CallLogCompat.INCOMING_TYPE) {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_call_avatar_received));
                } else if (type == CallLogCompat.MISSED_TYPE) {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_call_avatar_missed));
                } else if (type == CallLogCompat.REJECTED_TYPE) {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_call_avatar_rejected));
                } else {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_call_avatar));
                }

                long dateMills = callLogRecord.getDate();
                if (dateMills > System.currentTimeMillis()) {
                    Logger.w("Invalid date:%s, current:%s", dateMills, System.currentTimeMillis());
                    holder.getLineTwoTextView().setText(getContext().getString(R.string.title_backup_at_invalid_date));
                } else {
                    holder.getLineTwoTextView().setText(FuzzyDateTimeFormatter.getTimeAgo(getContext(), new Date(callLogRecord.getDate())));
                }
                super.onBindViewHolder(holder, record);
                if (!TextUtils.isEmpty(callLogRecord.getName())) {
                    holder.getLineOneTextView().setText(callLogRecord.getName());
                }
            }
        };
    }


}
