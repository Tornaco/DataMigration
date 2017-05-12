package org.newstand.datamigration.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.newstand.datamigration.R;

import lombok.Getter;
import tornaco.lib.widget.CheckableImageView;

/**
 * Created by Nick@NewStand.org on 2017/3/14 13:48
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class P2PListViewHolder extends RecyclerView.ViewHolder {

    @Getter
    private TextView lineOneTextView;
    @Getter
    private TextView lineTwoTextView;
    @Getter
    private CheckableImageView checkableImageView;

    public P2PListViewHolder(View itemView) {
        super(itemView);
        lineOneTextView = (TextView) itemView.findViewById(android.R.id.title);
        lineTwoTextView = (TextView) itemView.findViewById(android.R.id.text1);
        checkableImageView = (CheckableImageView) itemView.findViewById(R.id.checkable_img_view);
    }
}
