package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.newstand.datamigration.R;
import org.newstand.datamigration.cache.SelectionCache;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.fragment.CategoryViewerFragment;

public abstract class CategoryViewerActivity extends TransactionSafeActivity
        implements CategoryViewerFragment.OnCategorySelectListener,
        CategoryViewerFragment.OnSubmitListener, CategoryViewerFragment.LoaderSourceProvider {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
        replaceV4(R.id.container, new CategoryViewerFragment(), null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SelectionCache.from(this).cleanUp();
    }

    @Override
    public void onCategorySelect(DataCategory category) {
        Intent intent = new Intent(this, DataListHostActivity.class);
        intent.putExtra(IntentEvents.KEY_CATEGORY, category.name());
        intent.putExtra(IntentEvents.KEY_SOURCE, onRequestLoaderSource());
        startActivity(intent);
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

