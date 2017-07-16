package org.newstand.datamigration.data.model;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Nick@NewStand.org on 2017/4/19 13:35
 * E-Mail: NewStand@163.com
 * All right reserved.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class CallLogRecord extends FileBasedRecord {
    private String num;
    private String name;
    private long date;
    private int type;

    @Override
    public DataCategory category() {
        return DataCategory.CallLog;
    }

    @Override
    public long calculateSize() throws IOException {
        if (getPath() == null) {
            // A cal log backup file is estimated to 5kb.
            return 5 * 1024;
        }
        return super.calculateSize();
    }
}
