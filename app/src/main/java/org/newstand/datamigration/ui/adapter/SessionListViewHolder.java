package org.newstand.datamigration.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SessionListViewHolder extends RecyclerView.ViewHolder {

    @Getter
    private TextView lineOneTextView;

    @Getter
    private TextView lineTwoTextView;

    public SessionListViewHolder(View itemView) {
        super(itemView);
        lineOneTextView = (TextView) itemView.findViewById(android.R.id.title);
        lineTwoTextView = (TextView) itemView.findViewById(android.R.id.text1);
    }

}
