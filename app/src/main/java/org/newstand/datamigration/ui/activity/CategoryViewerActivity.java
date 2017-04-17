package org.newstand.datamigration.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.fragment.CategoryViewerFragment;

public abstract class CategoryViewerActivity extends TransitionSafeActivity
        implements CategoryViewerFragment.OnCategorySelectListener,
        CategoryViewerFragment.OnSubmitListener, CategoryViewerFragment.LoaderSourceProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
    }

    protected void showViewerFragment() {
        replaceV4(R.id.container, onCreateViewerFragment(), null);
    }

    protected Fragment onCreateViewerFragment() {
        return new CategoryViewerFragment();
    }

    abstract LoadingCacheManager getCache();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCache().clear();
    }

    @Override
    public void onCategorySelect(DataCategory category) {
        Intent intent = new Intent(this, getListHostActivityClz());
        intent.putExtra(IntentEvents.KEY_CATEGORY, category.name());
        intent.putExtra(IntentEvents.KEY_SOURCE, onRequestLoaderSource());
        transitionTo(intent);
    }

    protected Class<? extends Activity> getListHostActivityClz() {
        return DataListHostActivity.class;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSubmit() {
        // Nothing.
    }
}

