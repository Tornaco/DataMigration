package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.DataSenderManageFragment;
import org.newstand.datamigration.worker.transport.Session;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:34
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderActivity extends DataTransportActivity implements Producer<String>,
        DataSenderManageFragment.LoaderSourceProvider {

    @Getter
    private String host;

    @Override
    protected Fragment getTransportFragment() {
        return new DataSenderManageFragment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = getIntent().getStringExtra(IntentEvents.KEY_HOST);
    }

    @Override
    public String produce() {
        return getHost();
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return LoaderSource.builder().session(Session.create())
                .parent(LoaderSource.Parent.Received).build();
    }
}
