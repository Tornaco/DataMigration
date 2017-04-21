package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.HelpInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 17:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class HelpListAdapter extends RecyclerView.Adapter<CommonListViewHolder> {

    @Getter
    private final List<HelpInfo> helpInfos;

    @Getter
    private Context context;

    public HelpListAdapter(Context context) {
        this.helpInfos = new ArrayList<>();
        this.context = context;
    }


    public void update(Collection<HelpInfo> src) {
        synchronized (helpInfos) {
            helpInfos.clear();
            helpInfos.addAll(src);
        }
        onUpdate();
    }

    public void onUpdate() {
        notifyDataSetChanged();
    }

    @Override
    public CommonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.data_item_template_with_checkable, parent, false);
        final CommonListViewHolder holder = new CommonListViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder);
            }
        });
        return holder;
    }

    protected void onItemClick(CommonListViewHolder holder) {
    }

    @Override
    public void onBindViewHolder(CommonListViewHolder holder, final int position) {
        HelpInfo helpInfo = getHelpInfos().get(position);
        onBindViewHolder(holder, helpInfo);
    }

    protected void onBindViewHolder(CommonListViewHolder holder, final HelpInfo helpInfo) {
        holder.getLineOneTextView().setText(helpInfo.getQuestion());
        holder.getLineTwoTextView().setText(helpInfo.getAnswer());
        Glide.with(getContext()).load(helpInfo.getAskerAvatar())
                .error(R.mipmap.ic_help_avatar)
                .into(holder.getCheckableImageView());
    }

    @Override
    public int getItemCount() {
        return helpInfos.size();
    }
}