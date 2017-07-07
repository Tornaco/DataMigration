package org.newstand.datamigration.ui.fragment;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
            public void onBindViewHolder(final CommonListViewHolder holder, DataRecord record) {
                ContactRecord contactRecord = (ContactRecord) record;
                holder.getLineTwoTextView().setText(buildSummary(contactRecord));
                String id = contactRecord.getId();
                if (!TextUtils.isEmpty(id)) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                            Long.parseLong(id));
                    Glide.with(ContactListFragment.this).load(uri)
                            .crossFade()
                            .bitmapTransform(new CropCircleTransformation(getContext()))
                            .error(R.mipmap.ic_contacts_avatar)
                            .into(holder.getCheckableImageView());
                } else {
                    holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(),
                            R.mipmap.ic_contacts_avatar));
                }
                super.onBindViewHolder(holder, record);
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
