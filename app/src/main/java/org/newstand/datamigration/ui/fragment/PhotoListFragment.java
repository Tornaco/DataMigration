package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.PhotoRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;

import tornaco.lib.media.vinci.Vinci;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class PhotoListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Photo;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                PhotoRecord photoRecord = (PhotoRecord) record;
                holder.getLineTwoTextView().setText(Files.formatSize(photoRecord.getSize()));

                Vinci.load(getContext(), photoRecord.getPath())
                        .placeHolder(R.mipmap.ic_photo_avatar)
                        .error(R.mipmap.ic_photo_avatar)
                        .into(holder.getCheckableImageView());

                super.onBindViewHolder(holder, record);
            }
        };
    }
}
