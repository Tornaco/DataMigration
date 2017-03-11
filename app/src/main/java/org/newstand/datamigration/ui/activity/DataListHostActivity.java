package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.R;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.model.DataCategory;
import org.newstand.datamigration.model.message.EventDefinations;
import org.newstand.datamigration.ui.fragment.ContactListFragment;
import org.newstand.datamigration.ui.fragment.DataListViewerFragment;
import org.newstand.datamigration.ui.fragment.MusicListFragment;
import org.newstand.datamigration.ui.fragment.PhotoListFragment;
import org.newstand.datamigration.ui.fragment.VideoListFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataListHostActivity extends TransactionSafeActivity implements DataListViewerFragment.LoaderSourceProvider {

    private LoaderSource mLoaderSource;

    private void showList() {
        String extra = getIntent().getStringExtra(EventDefinations.KEY_CATEGORY);
        DataCategory category = DataCategory.valueOf(DataCategory.class, extra);
        Fragment fragment = getFragmentByCategory(category);
        placeFragment(R.id.container, fragment, null);
        setTitleWithTypeface(getString(category.nameRes()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        setTitleWithTypeface(getString(R.string.title_category));
    }

    @Override
    protected void onSmooth() {
        super.onSmooth();
        resolveIntent();
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
        mLoaderSource = intent.getParcelableExtra(EventDefinations.KEY_SOURCE);
        showList();
    }

    private Fragment getFragmentByCategory(DataCategory category) {
        switch (category) {
            case Contact:
                return new ContactListFragment();
            case Music:
                return new MusicListFragment();
            case Photo:
                return new PhotoListFragment();
            case Video:
                return new VideoListFragment();
            default:
                return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
