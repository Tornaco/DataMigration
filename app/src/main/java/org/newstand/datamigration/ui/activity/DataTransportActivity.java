package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.fragment.DataTransportFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/10 9:30
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataTransportActivity extends TransactionSafeActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
    }

    @Override
    protected void onSmooth() {
        super.onSmooth();
        placeFragment(R.id.container, new DataTransportFragment(), null);
    }
}
