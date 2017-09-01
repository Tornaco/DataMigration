package org.newstand.datamigration.ui.fragment;

import android.content.ContentUris;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.ContactRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.io.InputStream;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.CircleImageEffect;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;
import dev.tornaco.vangogh.loader.Loader;
import dev.tornaco.vangogh.loader.LoaderObserver;
import dev.tornaco.vangogh.media.BitmapImage;
import dev.tornaco.vangogh.media.Image;
import dev.tornaco.vangogh.media.ImageSource;

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

    private ContactsLoader contactsLoader = new ContactsLoader();

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
                    Vangogh.with(ContactListFragment.this).load(uri)
                            .effect(new CircleImageEffect())
                            .usingLoader(contactsLoader)
                            .fallback(R.mipmap.ic_contacts_avatar)
                            .applier(new FadeOutFadeInApplier())
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
                getString(R.string.phone_num_unknown)
                : String.valueOf(contactRecord.getPhoneNum());
        String email = contactRecord.getEmail();
        if (email == null) return phoneNum;
        else return phoneNum + "\n" + email;
    }


    class ContactsLoader implements Loader<Image> {

        @Nullable
        @Override
        public Image load(@NonNull ImageSource imageSource, @Nullable LoaderObserver loaderObserver) {
            String url = imageSource.getUrl();
            InputStream in =
                    ContactsContract.Contacts.openContactPhotoInputStream(getContext()
                            .getContentResolver(), Uri.parse(url));
            if (in != null) {
                return new BitmapImage(BitmapFactory.decodeStream(in));
            }
            return null;
        }

        @Override
        public int priority() {
            return 3;
        }
    }
}
