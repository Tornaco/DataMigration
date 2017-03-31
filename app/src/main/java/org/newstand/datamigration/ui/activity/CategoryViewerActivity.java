package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.nononsenseapps.filepicker.FilePickerActivity;

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
        replaceV4(R.id.container, new CategoryViewerFragment(), null);
    }

    abstract LoadingCacheManager getCache();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCache().clear();
    }

    @Override
    public void onCategorySelect(DataCategory category) {
        Intent intent = new Intent(this, DataListHostActivity.class);
        intent.putExtra(IntentEvents.KEY_CATEGORY, category.name());
        intent.putExtra(IntentEvents.KEY_SOURCE, onRequestLoaderSource());
        transitionTo(intent);
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

