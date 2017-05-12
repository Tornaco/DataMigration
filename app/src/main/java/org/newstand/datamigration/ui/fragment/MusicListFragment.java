package org.newstand.datamigration.ui.fragment;

import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.data.model.MusicRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;
import org.newstand.datamigration.utils.Files;


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
                MusicRecord musicRecord = (MusicRecord) record;
                String artist = musicRecord.getArtist();
                if (TextUtils.isEmpty(artist)) {
                    holder.getLineTwoTextView().setText(Files.formatSize(musicRecord.getSize()));
                } else {
                    holder.getLineTwoTextView().setText(artist);
                }

                Picasso.with(getContext())
                        .load(musicRecord.getArtUri())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_music_avatar)
                        .error(R.mipmap.ic_music_avatar)
                        .skipMemoryCache()
                        .into(holder.getCheckableImageView());

                super.onBindViewHolder(holder, record);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
