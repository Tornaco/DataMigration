package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.UserAction;
import org.newstand.datamigration.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/29 17:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class UserActionListAdapter extends RecyclerView.Adapter<CommonListViewHolder> {

    @Getter
    private final List<UserAction> userActions;

    @Getter
    private Context context;

    public UserActionListAdapter(Context context) {
        this.userActions = new ArrayList<>();
        this.context = context;
    }


    public void update(Collection<UserAction> src) {
        synchronized (userActions) {
            userActions.clear();
            userActions.addAll(src);
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
        UserAction action = getUserActions().get(position);
        onBindViewHolder(holder, action);
    }

    protected void onBindViewHolder(CommonListViewHolder holder, final UserAction action) {
        holder.getLineOneTextView().setText(action.getEventTitle());
        holder.getLineTwoTextView().setText(action.getEventDescription()
                + "\n"
                + DateUtils.formatLong(action.getDate()));
    }

    @Override
    public int getItemCount() {
        return userActions.size();
    }
}