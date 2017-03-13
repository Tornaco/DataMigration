package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.DataCategory;
import org.newstand.datamigration.data.event.EventDefinations;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.service.DataSelectionKeeperServiceProxy;
import org.newstand.datamigration.ui.fragment.CategorySelectionViewerFragment;
import org.newstand.datamigration.worker.backup.session.Session;

public class CategoryViewerActivity extends TransactionSafeActivity implements CategorySelectionViewerFragment.OnCategorySelectListener,
        CategorySelectionViewerFragment.OnSubmitListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHomeAsUp();
        setTitle(getTitle());
        setContentView(R.layout.activity_with_container_template);
        placeFragment(R.id.container, new CategorySelectionViewerFragment(), null);
        DataSelectionKeeperServiceProxy.start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataSelectionKeeperServiceProxy.stop(this);
    }

    LoaderSource onRequestSource() {
        return LoaderSource.builder().session(Session.create()).parent(LoaderSource.Parent.Android).build();
    }

    @Override
    public void onCategorySelect(DataCategory category) {
        Intent intent = new Intent(this, DataListHostActivity.class);
        intent.putExtra(EventDefinations.KEY_CATEGORY, category.name());
        intent.putExtra(EventDefinations.KEY_SOURCE, onRequestSource());
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

