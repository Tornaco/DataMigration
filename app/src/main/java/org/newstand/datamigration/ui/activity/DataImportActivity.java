package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.DataImportManageFragment;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataImportActivity extends DataTransportActivity implements DataImportManageFragment.LoaderSourceProvider {

    private LoaderSource mSource;

    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(mSource);
        Logger.d("Source = " + mSource);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveIntent();
    }

    @Override
    protected Fragment getTransportFragment() {
        return new DataImportManageFragment();
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return mSource;
    }
}
