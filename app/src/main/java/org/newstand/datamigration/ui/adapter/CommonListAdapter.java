package org.newstand.datamigration.ui.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.DataRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/7 15:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class CommonListAdapter extends RecyclerView.Adapter<CommonListViewHolder> {

    @Getter
    protected final List<DataRecord> dataRecords;

    @Getter
    private Context context;

    public CommonListAdapter(Context context) {
        this.dataRecords = new ArrayList<>();
        this.context = context;
    }

    public void selectAll(boolean select) {
        synchronized (dataRecords) {
            for (DataRecord c : dataRecords) {
                c.setChecked(select);
            }
        }
        notifyDataSetChanged();
    }

    public boolean hasSelection() {
        synchronized (dataRecords) {
            for (DataRecord dataRecord : dataRecords) {
                if (dataRecord.isChecked()) return true;
            }
        }
        return false;
    }

    public void update(Collection<DataRecord> src) {
        synchronized (dataRecords) {
            dataRecords.clear();
            dataRecords.addAll(src);
        }
        onUpdate();
    }

    public void onUpdate() {
        notifyDataSetChanged();
    }

    @Override
    public CommonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(getTemplateLayoutRes(), parent, false);
        final CommonListViewHolder holder = new CommonListViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(holder);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onItemLongClick(holder);
            }
        });
        holder.getCheckableImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckableImageClick(holder);
            }
        });
        return holder;
    }

    protected boolean onItemLongClick(CommonListViewHolder holder) {
        return false;
    }

    protected
    @LayoutRes
    int getTemplateLayoutRes() {
        return R.layout.data_item_template_with_checkable;
    }

    protected void onItemClick(CommonListViewHolder holder) {
        holder.getCheckableImageView().setChecked(!holder.getCheckableImageView().isChecked());
        onCheckStateChanged(holder.getCheckableImageView().isChecked(), holder.getAdapterPosition());
    }

    public void onCheckableImageClick(CommonListViewHolder holder) {
        onItemClick(holder);
    }

    protected void onCheckStateChanged(boolean checked, int position) {
        if (position < 0) {
            // This is a workaround to fix the issue, when adapter position is -1.
            // which means before/under init???
            return;
        }
        DataRecord r = getDataRecords().get(position);
        r.setChecked(checked);
    }

    @Override
    public void onBindViewHolder(CommonListViewHolder holder, final int position) {
        DataRecord r = getDataRecords().get(position);
        onBindViewHolder(holder, r);
    }

    protected void onBindViewHolder(CommonListViewHolder holder, final DataRecord r) {
        holder.getLineOneTextView().setText(r.getDisplayName());
        holder.getCheckableImageView().setChecked(r.isChecked(), false);
    }

    @Override
    public int getItemCount() {
        return dataRecords.size();
    }
}
