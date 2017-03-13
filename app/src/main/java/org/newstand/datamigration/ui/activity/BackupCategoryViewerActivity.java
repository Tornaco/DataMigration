package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Preconditions;

import org.newstand.datamigration.data.event.EventDefinations;
import org.newstand.datamigration.loader.LoaderSource;

/**
 * Created by Nick@NewStand.org on 2017/3/9 16:58
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class BackupCategoryViewerActivity extends CategoryViewerActivity {

    private LoaderSource mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        resolveIntent();
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        startActivity(new Intent(this, DataTransportActivity.class));
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(EventDefinations.KEY_SOURCE);
        Preconditions.checkNotNull(mSource);
    }

    @Override
    LoaderSource onRequestSource() {
        return mSource;
    }
}
