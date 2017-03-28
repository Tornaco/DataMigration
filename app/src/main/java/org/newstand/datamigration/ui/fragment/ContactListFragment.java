package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ContactListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Contact;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_contacts_avatar));
                super.onBindViewHolder(holder, record);
                ContactRecord contactRecord = (ContactRecord) record;
                holder.getLineTwoTextView().setText(buildSummary(contactRecord));
            }
        };
    }

    private String buildSummary(ContactRecord contactRecord) {
        String phoneNum = contactRecord.getPhoneNum() == null ?
                getString(R.string.phone_num_unknown) : String.valueOf(contactRecord.getPhoneNum());
        String email = contactRecord.getEmail();
        if (email == null) return phoneNum;
        else return phoneNum + "\n" + email;
    }
}
