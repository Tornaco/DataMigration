package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Nick@NewStand.org on 2017/3/23 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SenderCategoryViewerActivity extends AndroidCategoryViewerActivity {

    String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHost = getIntent().getStringExtra("host");
    }

    @Override
    public void onSubmit() {
        Intent intent = new Intent(this, DataSenderActivity.class);
        intent.putExtra("host", mHost);
        startActivity(intent);
    }
}
