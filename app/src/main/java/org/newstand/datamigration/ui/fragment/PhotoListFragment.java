package org.newstand.datamigration.ui.fragment;


import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.PhotoRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.ui.widget.ImageViewerDialog;
import org.newstand.datamigration.utils.Files;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.CircleImageEffect;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;

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
            public void onBindViewHolder(final CommonListViewHolder holder, DataRecord record) {
                PhotoRecord photoRecord = (PhotoRecord) record;
                holder.getLineTwoTextView().setText(Files.formatSize(photoRecord.getSize()));

                Vangogh.with(getContext())
                        .load(photoRecord.getPath())
                        .effect(new CircleImageEffect())
                        .applier(new FadeOutFadeInApplier())
                        .fallback(R.drawable.aio_image_default)
                        .into(holder.getCheckableImageView());

                super.onBindViewHolder(holder, record);
            }

            @Override
            protected boolean onItemLongClick(CommonListViewHolder holder) {
                // Show details.
                showDetailedImage(getDataRecords().get(holder.getAdapterPosition()));
                return true;
            }
        };
    }

    private void showDetailedImage(DataRecord record) {
        ImageViewerDialog.attach(getActivity(), record.getDisplayName(), ((PhotoRecord) record).getPath());
    }
}
