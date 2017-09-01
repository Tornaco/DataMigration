package org.newstand.datamigration.ui.fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.VideoRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.CircleImageEffect;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class VideoListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Video;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                VideoRecord videoRecord = (VideoRecord) record;
                holder.getLineTwoTextView().setText(Files.formatSize(videoRecord.getSize()));
                Vangogh.with(VideoListFragment.this)
                        .load(videoRecord.getPath())
                        .effect(new CircleImageEffect())
                        .applier(new FadeOutFadeInApplier())
                        .placeHolder(R.mipmap.ic_video_avatar)
                        .fallback(R.mipmap.ic_video_avatar)
                        .into(holder.getCheckableImageView());
                super.onBindViewHolder(holder, record);

            }
        };
    }
}
