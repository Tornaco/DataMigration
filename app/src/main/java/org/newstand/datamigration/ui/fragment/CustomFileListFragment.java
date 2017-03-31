package org.newstand.datamigration.ui.fragment;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.model.CustomFileRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/31 10:51
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class CustomFileListFragment extends DataListViewerFragment {

    @Override
    protected void onLoadFinish(Collection<DataRecord> collection) {
        super.onLoadFinish(collection);
        Snackbar.make(getRecyclerView(), R.string.title_only_root_dir, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show();
    }

    @Override
    DataCategory getDataType() {
        return DataCategory.CustomFile;
    }

    @Override
    CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(getContext()) {

            @Override
            protected int getTemplateLayoutRes() {
                return R.layout.data_item_template_with_checkable_single_line;
            }

            @Override
            public void onBindViewHolder(CommonListViewHolder holder, DataRecord record) {
                CustomFileRecord customFileRecord = (CustomFileRecord) record;
                holder.getCheckableImageView().setImageDrawable(
                        customFileRecord.isDir() ?
                                ContextCompat.getDrawable(getContext(), R.mipmap.ic_dir)
                                : ContextCompat.getDrawable(getContext(), R.mipmap.ic_file));
                super.onBindViewHolder(holder, record);
            }

            @Override
            protected void onItemClick(CommonListViewHolder holder) {
                DataRecord record = getAdapter().getDataRecords().get(holder.getAdapterPosition());
                CustomFileRecord customFileRecord = (CustomFileRecord) record;
                if (!customFileRecord.isDir()) {
                    super.onItemClick(holder);
                }
            }
        };
    }
}
