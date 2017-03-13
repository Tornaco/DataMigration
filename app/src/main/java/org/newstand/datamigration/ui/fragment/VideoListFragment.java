package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.DataRecord;
import org.newstand.datamigration.data.VideoRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;

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
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher));
                super.onBindViewHolder(holder, record);
                VideoRecord videoRecord = (VideoRecord) record;
                holder.getLineTwoTextView().setText(Files.formatSize(videoRecord.getSize()));
                Glide.with(VideoListFragment.this)
                        .load(videoRecord.getPath())
                        .centerCrop()
                        .animate(android.R.anim.fade_in)
                        .fallback(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.getCheckableImageView());

            }
        };
    }
}
