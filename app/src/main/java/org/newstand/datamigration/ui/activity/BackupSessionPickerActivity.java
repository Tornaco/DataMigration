package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.SmsContentProviderCompat;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.BackupSessionPickerFragment;
import org.newstand.datamigration.worker.transport.Session;

/**
 * Created by Nick@NewStand.org on 2017/3/9 15:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupSessionPickerActivity extends TransitionSafeActivity implements BackupSessionPickerFragment.OnSessionSelectListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
        replaceV4(R.id.container, new BackupSessionPickerFragment(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Let's Restore default SMS app.
        // Both OK if we are not SMS app or not.
        SmsContentProviderCompat.restoreDefSmsAppRetentionCheckedAsync(this);
    }

    @Override
    public void onSessionSelect(Session session) {
        Intent intent = new Intent(this, BackupCategoryViewerActivityCollapsing.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, LoaderSource.builder()
                .parent(LoaderSource.Parent.Backup).session(session).build());
        transitionTo(intent);
    }
}
