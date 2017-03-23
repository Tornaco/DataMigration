package org.newstand.datamigration.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.orhanobut.logger.Logger;

import org.newstand.datamigration.R;
import org.newstand.datamigration.data.event.IntentEvents;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.loader.LoaderSource;
import org.newstand.datamigration.ui.fragment.AppListFragment;
import org.newstand.datamigration.ui.fragment.ContactListFragment;
import org.newstand.datamigration.ui.fragment.DataListViewerFragment;
import org.newstand.datamigration.ui.fragment.MusicListFragment;
import org.newstand.datamigration.ui.fragment.PhotoListFragment;
import org.newstand.datamigration.ui.fragment.SmsListFragment;
import org.newstand.datamigration.ui.fragment.VideoListFragment;

/**
 * Created by Nick@NewStand.org on 2017/3/8 9:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataListHostActivity extends TransactionSafeActivity implements DataListViewerFragment.LoaderSourceProvider {

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
        setContentView(R.layout.activity_with_container_template);
        showHomeAsUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mLoaderSource = intent.getParcelableExtra(IntentEvents.KEY_SOURCE);
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
            case App:
                return new AppListFragment();
            case Sms:
                return new SmsListFragment();
            default:
                return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
