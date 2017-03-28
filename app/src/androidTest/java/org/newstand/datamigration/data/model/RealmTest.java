package org.newstand.datamigration.data.model;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.orhanobut.logger.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.utils.Collections;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Nick@NewStand.org on 2017/3/27 16:32
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@RunWith(AndroidJUnit4.class)
public class RealmTest {

    @Test
    public void testRealm() {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        // Clear the realm from last time
        Realm.deleteRealm(realmConfiguration);

        Realm realm = Realm.getInstance(realmConfiguration);

        realm.beginTransaction();

        for (int i = 0; i < 100; i++) {
            Dummy dummy = realm.createObject(Dummy.class);
            dummy.setId(i);
            dummy.setName("N@" + i);
        }

        realm.commitTransaction();

        // Query
        RealmResults<Dummy> guests = realm.where(Dummy.class).findAll();
        realm.beginTransaction();

        Collections.consumeRemaining(guests, new Consumer<Dummy>() {
            @Override
            public void consume(@NonNull Dummy d) {
                Logger.d(d);
            }
        });

        realm.commitTransaction();

    }

    @Test
    public void testRealm2() {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        // Clear the realm from last time
        Realm.deleteRealm(realmConfiguration);

        Realm realm = Realm.getInstance(realmConfiguration);

        realm.beginTransaction();

        for (int i = 0; i < 100; i++) {
            Dummy2 dummy = realm.createObject(Dummy2.class);
            dummy.setChecked(true);
            dummy.setDir("DIR-" + i);
        }

        realm.commitTransaction();

        // Query
        RealmResults<Dummy2> guests = realm.where(Dummy2.class).findAll();
        realm.beginTransaction();

        Collections.consumeRemaining(guests, new Consumer<Dummy2>() {
            @Override
            public void consume(@NonNull Dummy2 d) {
                Logger.d(d);
            }
        });

        realm.commitTransaction();

    }
}