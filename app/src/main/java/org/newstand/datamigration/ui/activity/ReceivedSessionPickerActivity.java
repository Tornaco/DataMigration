package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.ReceivedSessionPickerFragment;
import org.newstand.datamigration.worker.transport.Session;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ReceivedSessionPickerActivity extends TransitionSafeActivity implements ReceivedSessionPickerFragment.OnSessionSelectListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
        replaceV4(R.id.container, new ReceivedSessionPickerFragment(), null);
    }

    @Override
    public void onSessionSelect(Session session) {
        Intent intent = new Intent(this, ReceivedCategoryViewerActivity.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, LoaderSource.builder()
                .parent(LoaderSource.Parent.Received).session(session).build());
        transitionTo(intent);
    }
}
