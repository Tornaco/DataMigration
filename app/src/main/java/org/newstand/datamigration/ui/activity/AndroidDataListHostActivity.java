package org.newstand.datamigration.ui.activity;

import android.support.v4.app.Fragment;

import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.ui.fragment.AlarmListFragment;
import org.newstand.datamigration.ui.fragment.AppListFragment;
import org.newstand.datamigration.ui.fragment.CallListFragment;
import org.newstand.datamigration.ui.fragment.ContactListFragment;
import org.newstand.datamigration.ui.fragment.CustomFileListFragment;
import org.newstand.datamigration.ui.fragment.MusicListFragment;
import org.newstand.datamigration.ui.fragment.PhotoListFragment;
import org.newstand.datamigration.ui.fragment.SmsListFragment;
import org.newstand.datamigration.ui.fragment.VideoListFragment;
import org.newstand.datamigration.ui.fragment.WifiListFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/7 15:20
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AndroidDataListHostActivity extends DataListHostActivity {
    @Override
    protected Fragment getFragmentByCategory(DataCategory category) {
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
            case CallLog:
                return new CallListFragment();
            case Alarm:
                return new AlarmListFragment();
            case Wifi:
                return new WifiListFragment();
            case CustomFile:
                return new CustomFileListFragment();
            default:
                throw new UnsupportedOperationException("UnSupported category " + category);
        }
    }
}
