package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.logger.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListViewHolder> {

    @Getter
    private final List<Session> sessionList;

    @Getter
    private Context context;

    public SessionListAdapter(Context context) {
        this.sessionList = new ArrayList<>();
        this.context = context;
    }

    public void update(Collection<Session> src) {
        synchronized (sessionList) {
            sessionList.clear();
            sessionList.addAll(src);
        }
        update();
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public SessionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.data_item_template_with_checkable, parent, false);
        final SessionListViewHolder holder = new SessionListViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder);
            }
        });
        return holder;
    }

    protected void onItemClick(SessionListViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(SessionListViewHolder holder, final int position) {
        Session s = getSessionList().get(position);
        onBindViewHolder(holder, s);
    }

    protected void onBindViewHolder(SessionListViewHolder holder, final Session r) {
        holder.getImageView().setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_bk_session));
        holder.getLineOneTextView().setText(r.getName());
        // Validate the session date.
        long dateMills = r.getDate();
        if (dateMills > System.currentTimeMillis()) {
            Logger.w("Invalid date:%s, current:%s", dateMills, System.currentTimeMillis());
            holder.getLineTwoTextView().setText(getContext().getString(R.string.title_backup_at_invalid_date));
        } else {
            holder.getLineTwoTextView().setText(getContext().getString(R.string.title_backup_at,
                    FuzzyDateTimeFormatter.getTimeAgo(getContext(), new Date(r.getDate()))));
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }
}
