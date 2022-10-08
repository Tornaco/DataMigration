package org.newstand.datamigration.ui.fragment;

import android.icu.text.SimpleDateFormat;
import androidx.core.content.ContextCompat;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.MsgBox;
import org.newstand.datamigration.data.model.SMSRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.sql.Date;

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
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_sms_avatar));
                super.onBindViewHolder(holder, record);
                SMSRecord smsRecord = (SMSRecord) record;
                holder.getLineOneTextView().setText(smsRecord.getMsg());
                holder.getLineTwoTextView().setText(buildSummary(smsRecord));
            }
        };
    }

    private String buildSummary(SMSRecord smsRecord) {
        String addr = smsRecord.getAddr();
        MsgBox box = smsRecord.getMsgBox();
        String time = smsRecord.getTime();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d1 = new Date(Long.parseLong(time));
            time = format.format(d1);
        } else {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d1 = new Date(Long.parseLong(time));
            time = format.format(d1);
        }

        switch (box) {
            case SENT:
                return getString(R.string.summary_sms_template_send, addr, time);
            case INBOX:
                return getString(R.string.summary_sms_template_rec, addr, time);
            case DRAFT:
                return getString(R.string.summary_sms_template_draft, addr, time);
        }

        return getString(R.string.unknown);
    }
}
