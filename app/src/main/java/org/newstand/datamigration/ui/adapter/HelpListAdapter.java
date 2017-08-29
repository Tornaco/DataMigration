package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.HelpInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.tornaco.vangogh.Vangogh;
import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 17:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class HelpListAdapter extends RecyclerView.Adapter<HelpViewHolder> {

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
            for (HelpInfo i : src) {
                if (i != null) {
                    helpInfos.add(i);
                }
            }
        }
        onUpdate();
    }

    public void onUpdate() {
        notifyDataSetChanged();
    }

    @Override
    public HelpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        final HelpViewHolder holder = new HelpViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder);
            }
        });
        return holder;
    }

    protected void onItemClick(HelpViewHolder holder) {
    }

    @Override
    public void onBindViewHolder(HelpViewHolder holder, final int position) {
        HelpInfo helpInfo = getHelpInfos().get(position);
        onBindViewHolder(holder, helpInfo);
    }

    protected void onBindViewHolder(HelpViewHolder holder, final HelpInfo helpInfo) {
        holder.getUserNameView().setText(helpInfo.getQuestion());
        holder.getFeedText().setText(helpInfo.getAnswer().getText());
        holder.getFeedText().setAutoLinkMask(Linkify.ALL);
        Vangogh.with(getContext()).load(helpInfo.getAskerAvatar())
                .fallback(R.mipmap.ic_help_avatar)
                .into(holder.getUserProfileView());
        if (helpInfo.getAnswer().getImageUrls() != null && helpInfo.getAnswer().getImageUrls().length > 0) {
            holder.getFeedImageView().setVisibility(View.VISIBLE);
            Vangogh.with(getContext()).load(helpInfo.getAnswer().getImageUrls()[0])
                    .fallback(R.mipmap.ic_help_avatar)
                    .into(holder.getFeedImageView());
        } else {
            holder.getFeedImageView().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return helpInfos.size();
    }
}