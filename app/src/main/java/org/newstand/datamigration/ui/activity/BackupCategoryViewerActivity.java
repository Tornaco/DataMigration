package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.orhanobut.logger.Logger;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
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
    LoadingCacheManager getCache() {
        return LoadingCacheManager.bk();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showViewerFragment();
    }

    @Override
    public void onSubmit() {
        super.onSubmit();
        Intent intent = new Intent(this, DataImportActivity.class);
        intent.putExtra(IntentEvents.KEY_SOURCE, mSource);
        transitionTo(intent);
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        mSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        Preconditions.checkNotNull(mSource);
        Logger.d("Source = " + mSource);
        LoadingCacheManager.createBK(getApplicationContext(), mSource.getSession());
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return mSource;
    }

}
