package org.newstand.datamigration.ui.fragment;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    DataCategory getDataType() {
        return DataCategory.Music;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {

            @Override
            public void onBindViewHolder(final CommonListViewHolder holder, DataRecord record) {
                MusicRecord musicRecord = (MusicRecord) record;
                String artist = musicRecord.getArtist();
                if (TextUtils.isEmpty(artist)) {
                    holder.getLineTwoTextView().setText(Files.formatSize(musicRecord.getSize()));
                } else {
                    holder.getLineTwoTextView().setText(artist);
                }

                Glide.with(getContext())
                        .load(musicRecord.getArtUri())
                        .centerCrop()
                        .error(R.mipmap.ic_music_avatar)
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
