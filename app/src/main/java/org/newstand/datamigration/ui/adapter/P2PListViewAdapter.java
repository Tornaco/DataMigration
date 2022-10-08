package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.Peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/14 13:48
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class P2PListViewAdapter extends RecyclerView.Adapter<P2PListViewHolder> {

    @Getter
    private final List<Peer> peerList;

    @Getter
    private Context context;

    public P2PListViewAdapter(Context context) {
        this.peerList = new ArrayList<>();
        this.context = context;
    }

    public void update(Collection<Peer> src) {
        synchronized (peerList) {
            peerList.clear();
            peerList.addAll(src);
        }
        update();
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public P2PListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.data_item_template_with_checkable, parent, false);
        final P2PListViewHolder holder = new P2PListViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder);
            }
        });
        return holder;
    }

    protected void onItemClick(P2PListViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(P2PListViewHolder holder, final int position) {
        Peer peer = getPeerList().get(position);
        onBindViewHolder(holder, peer);
    }

    protected void onBindViewHolder(P2PListViewHolder holder, final Peer peer) {
        holder.getLineOneTextView().setText(peer.getDevice().deviceName);
        holder.getLineTwoTextView().setText(peer.getDevice().deviceAddress);
        holder.getCheckableImageView().setImageDrawable(peer.getIcon());
    }

    @Override
    public int getItemCount() {
        return peerList.size();
    }
}
