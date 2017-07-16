package org.newstand.datamigration.ui.activity;

import android.content.Intent;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.event.TransportEventRecord;
import org.newstand.datamigration.data.model.CategoryRecord;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.repo.TransportEventRecordRepoService;

import java.util.List;

/**
 * Created by Tornaco on 2017/7/14.
 * Licensed with Apache.
 */

public class TransportFailureStatsViewerActivity extends TransportStatsViewerActivity {
    @Override
    protected List<TransportEventRecord> onQueryEvents(TransportEventRecordRepoService service, DataCategory category) {
        return service.fails(this, category);
    }

    @Override
    protected void onCategorySelect(CategoryRecord cr) {
        Intent intent = new Intent(this, TransportStatsFailureDetailsViewerActivity.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, getSession());
        intent.putExtra(IntentEvents.KEY_CATEGORY, cr.category().name());
        intent.putExtra(IntentEvents.KEY_TRANSPORT_TYPE, getTransportType().name());
        startActivity(intent);
    }
}
