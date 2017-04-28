package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Producer;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.DataReceiverManageFragment;
import org.newstand.datamigration.utils.DateUtils;
import org.newstand.datamigration.worker.transport.Session;

import lombok.Getter;

/**
 * Created by Nick@NewStand.org on 2017/3/21 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataReceiverActivity extends DataTransportActivity implements Producer<String>,
        DataReceiverManageFragment.LoaderSourceProvider {

    @Getter
    private String host;

    @Override
    protected Fragment getTransportFragment() {
        return new DataReceiverManageFragment();
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
        return LoaderSource.builder().session(Session.from(getString(R.string.title_received_default_name)
                + "@" + DateUtils.formatForFileName(System.currentTimeMillis())))
                .parent(LoaderSource.Parent.Android).build();
    }
}
