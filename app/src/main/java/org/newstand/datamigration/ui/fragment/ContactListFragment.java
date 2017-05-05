package org.newstand.datamigration.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.io.InputStream;

import tornaco.lib.media.vinci.Vinci;
import tornaco.lib.media.vinci.effect.FadeInViewAnimator;
import tornaco.lib.media.vinci.loader.Loader;
import tornaco.lib.media.vinci.loader.Priority;

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


                final Uri uri = contactRecord.getUri();

                if (uri != null) {
                    Vinci.load(getContext(), "contact://" + contactRecord.getId())
                            .loader(new Loader() {
                                @Nullable
                                @Override
                                public Bitmap load(@NonNull String sourceUrl) {
                                    InputStream in =
                                            ContactsContract.Contacts.openContactPhotoInputStream(getContext()
                                                    .getContentResolver(), uri);
                                    if (in != null) {
                                        return BitmapFactory.decodeStream(in);
                                    }
                                    return null;
                                }

                                @Override
                                public int priority() {
                                    return Priority.C;
                                }
                            })
                            .placeHolder(R.mipmap.ic_contacts_avatar)
                            .error(R.mipmap.ic_contacts_avatar)
                            .animator(new FadeInViewAnimator())
                            .into(holder.getCheckableImageView());
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
