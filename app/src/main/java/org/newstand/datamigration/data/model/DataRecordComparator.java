package org.newstand.datamigration.data.model;

import org.newstand.datamigration.utils.PinyinComparator;

import java.util.Comparator;

/**
 * Created by guohao4 on 2017/7/5.
 */

public class DataRecordComparator implements Comparator<DataRecord> {
    @Override
    public int compare(DataRecord o1, DataRecord o2) {
        return new PinyinComparator().compare(o1.getDisplayName(), o2.getDisplayName());
    }
}
