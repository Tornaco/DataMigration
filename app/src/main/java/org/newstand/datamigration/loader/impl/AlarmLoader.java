package org.newstand.datamigration.loader.impl;

import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.loader.LoaderFilter;
import org.newstand.datamigration.worker.transport.Session;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Nick@NewStand.org on 2017/3/12 20:41
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AlarmLoader extends BaseLoader {

    @Override
    public Collection<DataRecord> loadFromAndroid(LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        DataRecord dummy = new DataRecord();
        dummy.setDisplayName("Dummy");
        records.add(dummy);
        return records;
    }

    @Override
    public Collection<DataRecord> loadFromSession(Session session, LoaderFilter<DataRecord> filter) {
        final Collection<DataRecord> records = new ArrayList<>();
        DataRecord dummy = new DataRecord();
        dummy.setDisplayName("Dummy");
        records.add(dummy);
        return records;
    }

    @Override
    public String[] needPermissions() {
        return new String[0];
    }
}
