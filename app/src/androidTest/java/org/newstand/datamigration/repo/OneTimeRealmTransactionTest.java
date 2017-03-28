package org.newstand.datamigration.repo;

import org.junit.Test;
import org.newstand.datamigration.data.model.Dummy;

/**
 * Created by Nick@NewStand.org on 2017/3/28 13:59
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
public class OneTimeRealmTransactionTest {
    @Test
    public void transaction() throws Exception {
        OneTimeRealmTransaction<Dummy> oneTimeRealmTransaction = new OneTimeRealmTransaction<>();
        oneTimeRealmTransaction.transaction()
                .event()
                .where(Dummy.class)
                .findAll();

    }

}