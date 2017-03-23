package org.newstand.datamigration.ui.activity;

import android.content.Intent;

/**
 * Created by Nick@NewStand.org on 2017/3/23 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class SenderCategoryViewerActivity extends AndroidCategoryViewerActivity {
    @Override
    public void onSubmit() {
        startActivity(new Intent(this, DataSenderActivity.class));
    }
}
