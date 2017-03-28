package org.newstand.datamigration.repo;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/3/28 13:53
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class OneTimeRealmTransaction<T> {

    private Realm newRealm() {
        return Realm.getInstance(onCreateConfig());
    }

    protected RealmConfiguration onCreateConfig() {
        return new RealmConfiguration.Builder().build();
    }

    Transaction<Realm> transaction() {
        Transaction<Realm> transaction = new Transaction<>(newRealm());
        return transaction;
    }

    static class Holder<T> {
        @Setter
        @Getter
        T t;

        Holder(T t) {
            this.t = t;
        }
    }
}
