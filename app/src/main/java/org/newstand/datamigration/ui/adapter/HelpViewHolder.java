package org.newstand.datamigration.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by Nick on 2017/6/20 17:38
 */
@Getter
public class HelpViewHolder extends RecyclerView.ViewHolder {

    ImageView userProfileView;
    ImageView feedImageView;
    TextView feedText;
    TextView userNameView;

    public HelpViewHolder(View itemView) {
        super(itemView);
        userProfileView = (ImageView) itemView.findViewById(R.id.user_profile_img);
        feedImageView = (ImageView) itemView.findViewById(R.id.feed_img);
        feedText = (TextView) itemView.findViewById(R.id.feed_text);
        userNameView = (TextView) itemView.findViewById(R.id.user_profile_name);
    }

}
