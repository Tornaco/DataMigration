package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.DataImportFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/15 16:37
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataImportActivity extends DataTransportActivity implements DataImportFragment.LoaderSourceProvider {

    private LoaderSource mSource;

    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
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
        return new DataImportFragment();
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return null;
    }
}
