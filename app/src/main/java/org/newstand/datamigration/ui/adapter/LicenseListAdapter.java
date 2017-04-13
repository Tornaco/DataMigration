package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.License;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 17:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class LicenseListAdapter extends RecyclerView.Adapter<CommonListViewHolder> {

    @Getter
    private final List<License> licenseList;

    @Getter
    private Context context;

    public LicenseListAdapter(Context context) {
        this.licenseList = new ArrayList<>();
        this.context = context;
    }


    public void update(Collection<License> src) {
        synchronized (licenseList) {
            licenseList.clear();
            licenseList.addAll(src);
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
        License license = getLicenseList().get(position);
        onBindViewHolder(holder, license);
    }

    protected void onBindViewHolder(CommonListViewHolder holder, final License license) {
        holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.github));
        holder.getLineOneTextView().setText(license.getTitle() + "@" + license.getAuthor());
        holder.getLineTwoTextView().setAutoLinkMask(Linkify.WEB_URLS);
        holder.getLineTwoTextView().setText(license.getDescription() + "\n\n" + license.getUrl());
    }

    @Override
    public int getItemCount() {
        return licenseList.size();
    }
}