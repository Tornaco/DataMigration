package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.provider.ThemeColor;
import org.newstand.datamigration.ui.fragment.DataListViewerFragment;
import org.newstand.logger.Logger;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataListHostActivity extends TransitionSafeActivity implements DataListViewerFragment.LoaderSourceProvider {

    private LoaderSource mLoaderSource;

    private void showList() {
        String extra = getIntent().getStringExtra(IntentEvents.KEY_CATEGORY);
        DataCategory category = DataCategory.valueOf(DataCategory.class, extra);
        Fragment fragment = getFragmentByCategory(category);
        replaceV4(R.id.container, fragment, null);
        setTitle(getString(category.nameRes()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getThemeColor()) {
            case White:
                setContentView(R.layout.activity_data_list_host);

                break;
            default:
                setContentView(R.layout.activity_data_list_host);

                break;
        }
        Toolbar toolbar = findView(R.id.toolbar);
        setSupportActionBar(toolbar);
        showHomeAsUp();

        resolveIntent();
    }

    @Override
    protected void onApplyTheme(ThemeColor color) {
        int themeRes = getAppThemeNoActionBar(color);
        setTheme(themeRes);
    }

    @Override
    public LoaderSource onRequestLoaderSource() {
        return mLoaderSource;
    }

    private void resolveIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            Logger.e("No intent, ignored.");
            return;
        }
        mLoaderSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
        showList();
    }

    protected Fragment getFragmentByCategory(DataCategory category) {
        throw new UnsupportedOperationException("Not support without impl...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
