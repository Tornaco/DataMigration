package org.newstand.datamigration.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.newstand.datamigration.R;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SessionListViewHolder extends RecyclerView.ViewHolder {

    @Getter
    private View actionView;

    @Getter
    private TextView lineOneTextView;

    @Getter
    private TextView lineTwoTextView;

    @Getter
    private ImageView imageView;

    public SessionListViewHolder(View itemView) {
        super(itemView);
        lineOneTextView = (TextView) itemView.findViewById(android.R.id.title);
        lineTwoTextView = (TextView) itemView.findViewById(android.R.id.text1);
        actionView = itemView.findViewById(R.id.action_more);
        actionView.setVisibility(View.VISIBLE);
        imageView = (ImageView) itemView.findViewById(R.id.checkable_img_view);
    }

}
