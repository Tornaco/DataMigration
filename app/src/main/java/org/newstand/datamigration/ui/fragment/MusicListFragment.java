package org.newstand.datamigration.ui.fragment;

import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;

import org.newstand.datamigration.R;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.DataRecord;
import org.newstand.datamigration.model.MusicRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class MusicListFragment extends DataListViewerFragment {

    @Override
    DataCategory getDataType() {
        return DataCategory.Music;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {
            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher));
                super.onBindViewHolder(holder, record);
                MusicRecord musicRecord = (MusicRecord) record;
                holder.getLineTwoTextView().setText(musicRecord.getArtist());
                Glide.with(MusicListFragment.this)
                        .load(musicRecord.getArtUri())
                        .centerCrop()
                        .fallback(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(holder.getCheckableImageView());

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
