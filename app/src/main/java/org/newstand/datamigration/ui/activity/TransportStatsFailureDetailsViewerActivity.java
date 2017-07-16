package org.newstand.datamigration.ui.activity;

import android.support.v4.content.ContextCompat;

import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;
import org.newstand.datamigration.ui.adapter.CommonListAdapter;
import org.newstand.datamigration.ui.adapter.CommonListViewHolder;

import java.util.List;

/**
 * Created by Tornaco on 2017/7/14.
 * Licensed with Apache.
 */

public class TransportStatsFailureDetailsViewerActivity extends TransportStatsDetailsViewerActivity {

    @Override
    protected List<TransportEventRecord> onQueryEvents(TransportEventRecordRepoService service, DataCategory category) {
        return service.fails(this, category);
    }

    @Override
    protected CommonListAdapter onCreateAdapter() {
        return new CommonListAdapter(this) {
            @Override
            protected void onBindViewHolder(CommonListViewHolder holder, DataRecord r) {
                holder.getLineOneTextView().setText(r.getDisplayName());
                holder.getCheckableImageView().setImageDrawable(ContextCompat.getDrawable(getContext(),
                        getCategory().iconRes()));
                holder.getLineTwoTextView().setText(((TransportEventRecord) r).getErrTrace());
            }

            @Override
            protected void onItemClick(CommonListViewHolder holder) {
                int position = holder.getAdapterPosition();
                if (position < 0) {
                    // This is a workaround to fix the issue, when adapter position is -1.
                    // which means before/under init???
                    return;
                }
                DataRecord record = getAdapter().getDataRecords().get(position);
                onRecordSelect(record);
            }
        };
    }
}
